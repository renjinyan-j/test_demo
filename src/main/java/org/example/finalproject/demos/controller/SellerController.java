package org.example.finalproject.demos.controller;

import org.example.finalproject.demos.pojo.Order;
import org.example.finalproject.demos.pojo.ProductWithImg;
import org.example.finalproject.demos.pojo.Products;
import org.example.finalproject.demos.service.CategoriesService;
import org.example.finalproject.demos.service.OrderService;
import org.example.finalproject.demos.service.ProductsService;
import org.example.finalproject.demos.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.nio.file.Paths;

@Controller
@RequestMapping("/seller")
@PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
public class SellerController {
    /**
     * 避免将前端上传的 MultipartFile 直接绑定到 Products.images(List<Images>) 字段，
     * 否则 Spring 会尝试将 MultipartFile 转成 Images 导致类型转换异常。
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("images");
    }

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductsService productsService;

    @Autowired
    private CategoriesService categoriesService;

    @Autowired
    private SecurityUtil securityUtil;

    // 图片上传路径：使用项目运行目录下的 uploads 文件夹，避免相对路径指向 Tomcat 临时目录
    private static final String UPLOAD_DIR = Paths.get(System.getProperty("user.dir"), "uploads").toString() + File.separator;

    /**
     * 卖家系统首页
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return "redirect:/toLoginPage";
        }

        // 可以添加统计信息，如商品总数、订单总数等
        int productCount = productsService.getProductsBySellerId(userId.intValue()).size();
        model.addAttribute("productCount", productCount);

        return "seller/dashboard";
    }

    /**
     * 显示商品上传页面
     */
    @GetMapping("/product/add")
    public String showAddProductForm(Model model) {
        // 获取所有分类供选择
        model.addAttribute("categories", categoriesService.getAllCategories());
        model.addAttribute("product", new Products());
        return "seller/product-form";
    }

    /**
     * 处理商品上传
     */
    @PostMapping("/product/add")
    public String addProduct(
            @ModelAttribute Products product,
            @RequestParam("images") MultipartFile[] images,
            RedirectAttributes redirectAttributes) {

        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return "redirect:/toLoginPage";
        }

        try {
            // 1. 设置卖家ID
            product.setUserId(userId.intValue());

            // 2. 处理图片上传
            List<String> imageUrls = new ArrayList<>();
            if (images != null && images.length > 0) {
                // 确保上传目录存在
                File uploadDir = new File(UPLOAD_DIR);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                for (MultipartFile image : images) {
                    if (!image.isEmpty()) {
                        // 生成唯一文件名
                        String originalFilename = image.getOriginalFilename();
                        if (originalFilename == null || originalFilename.isEmpty()) {
                            continue;
                        }
                        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                        String newFilename = UUID.randomUUID().toString() + extension;

                        // 保存文件
                        File file = new File(UPLOAD_DIR + newFilename);
                        image.transferTo(file);

                        // 保存URL（相对于static目录）
                        imageUrls.add("/uploads/" + newFilename);
                    }
                }
            }

            // 3. 保存商品和图片
            int result = productsService.addProduct(product, imageUrls);

            if (result > 0) {
                redirectAttributes.addFlashAttribute("msg", "商品上传成功！");
                return "redirect:/seller/product/list";
            } else {
                redirectAttributes.addFlashAttribute("error", "商品上传失败！");
                return "redirect:/seller/product/add";
            }
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "图片上传失败：" + e.getMessage());
            return "redirect:/seller/product/add";
        }
    }

    /**
     * 我的商品列表
     */
    @GetMapping("/product/list")
    public String myProducts(Model model) {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return "redirect:/toLoginPage";
        }

        List<ProductWithImg> products = productsService.getProductsBySellerId(userId.intValue());
        model.addAttribute("products", products);
        return "seller/product-list";
    }

    /**
     * 显示商品编辑页面
     */
    @GetMapping("/product/edit/{id}")
    public String showEditProductForm(@PathVariable Integer id, Model model) {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return "redirect:/toLoginPage";
        }

        ProductWithImg product = productsService.getProductById(id);
        if (product == null || !product.getUserId().equals(userId.intValue())) {
            return "redirect:/seller/product/list";
        }

        model.addAttribute("product", product);
        model.addAttribute("categories", categoriesService.getAllCategories());
        return "seller/product-form";
    }

    /**
     * 处理商品更新
     */
    @PostMapping("/product/edit/{id}")
    public String updateProduct(
            @PathVariable Integer id,
            @ModelAttribute Products product,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            RedirectAttributes redirectAttributes) {

        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return "redirect:/toLoginPage";
        }

        try {
            product.setId(id);
            product.setUserId(userId.intValue());

            // 处理新上传的图片
            List<String> imageUrls = new ArrayList<>();
            if (images != null && images.length > 0) {
                File uploadDir = new File(UPLOAD_DIR);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                for (MultipartFile image : images) {
                    if (!image.isEmpty()) {
                        String originalFilename = image.getOriginalFilename();
                        if (originalFilename == null || originalFilename.isEmpty()) {
                            continue;
                        }
                        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                        String newFilename = UUID.randomUUID().toString() + extension;

                        File file = new File(UPLOAD_DIR + newFilename);
                        image.transferTo(file);

                        imageUrls.add("/uploads/" + newFilename);
                    }
                }
            }

            int result = productsService.updateProduct(product, imageUrls);

            if (result > 0) {
                redirectAttributes.addFlashAttribute("msg", "商品更新成功！");
            } else {
                redirectAttributes.addFlashAttribute("error", "商品更新失败！");
            }

            return "redirect:/seller/product/list";
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "图片上传失败：" + e.getMessage());
            return "redirect:/seller/product/edit/" + id;
        }
    }

    /**
     * 删除商品
     */
    @PostMapping("/product/delete/{id}")
    public String deleteProduct(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return "redirect:/toLoginPage";
        }

        int result = productsService.deleteProduct(id, userId.intValue());

        if (result > 0) {
            redirectAttributes.addFlashAttribute("msg", "商品删除成功！");
        } else {
            redirectAttributes.addFlashAttribute("error", "商品删除失败！");
        }

        return "redirect:/seller/product/list";
    }

    /**
     * 我的订单列表（卖家发布的商品的订单）
     */
    @GetMapping("/order/list")
    public String myOrders(Model model) {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return "redirect:/toLoginPage";
        }

        // 查询卖家商品的订单（需要根据实际OrderService的方法调整）
        List<Order> orders = orderService.getOrdersBySellerId(userId.intValue());
        model.addAttribute("orders", orders);
        return "seller/order-list";
    }
}
