package org.example.finalproject.demos.config;

import org.example.finalproject.demos.pojo.Permission;
import org.example.finalproject.demos.service.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Component
//FilterInvocationSecurityMetadataSource接口的自定义实现类：
//用于根据请求URL和HTTP方法加载对应的权限配置
public class CustomFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

//    创建一个日志记录器，用于在程序中记录日志信息
    private static final Logger logger = LoggerFactory.getLogger(CustomFilterInvocationSecurityMetadataSource.class);

    // 权限集合：key为"请求方式:URL"（如"GET:/users"），value为对应权限
//    permissionMap存储了系统中所有的权限配置，
//    其中key是由请求方法和URL组成的字符串，value是一个权限集合，表示访问该URL所需的权限。
    private Map<String, Collection<ConfigAttribute>> permissionMap;

    @Autowired
    private PermissionService permissionService;

    // 初始化权限映射（启动时加载）
    @PostConstruct
    public void init() {
        loadPermissionMap();
    }

    /**
     * 加载所有权限，构建"请求方式:URL"→权限的映射（核心修复）
     */
    private void loadPermissionMap() {
//        初始化HashMap，用于存储权限映射
        permissionMap = new HashMap<>();
        try {
//            从数据库或其他数据源获取所有权限配置
            List<Permission> permissions = permissionService.getAllPermissions();

            // 增强健壮性：处理权限列表为null的情况
            if (permissions == null) {
                permissions = new ArrayList<>();
            }

//            记录日志，输出加载的权限数量
            logger.info("Starting to load permissions, total count: {}", permissions.size());

            for (Permission permission : permissions) {
                // 增强健壮性：检查Permission对象是否为null
                if (permission == null) {
                    continue;
                }

//                提取权限的URL、方法和权限码
                String url = permission.getUrl();
                String method = permission.getMethod();
                String permissionCode = permission.getPermissionCode();

                // 跳过无效权限配置
                if (url == null || url.trim().isEmpty() || permissionCode == null) {
                    continue;
                }

                // 处理请求方式：空值用"*"（匹配所有方法），统一转为大写
                String methodKey = (method == null || method.trim().isEmpty())
                        ? "*"
                        : method.trim().toUpperCase();

                // 构建联合key："请求方式:URL"（如"GET:/users"、"POST:/users"）
                String key = methodKey + ":" + url.trim();

                // 同一key可对应多个权限（如同时需要user:view和user:manage）
                Collection<ConfigAttribute> configAttributes = permissionMap.getOrDefault(key, new ArrayList<>());
//                SecurityConfig(permissionCode)：将权限码封装为SecurityConfig对象
                configAttributes.add(new SecurityConfig(permissionCode));
                permissionMap.put(key, configAttributes);
            }
//           记录日志，输出加载的权限数量
            logger.info("Finished loading permissions, map size: {}", permissionMap.size());
        } catch (Exception e) {
            logger.error("Error occurred while loading permissions", e);
            // 确保即使出现异常，permissionMap也是一个有效的HashMap
            if (permissionMap == null) {
                permissionMap = new HashMap<>();
            }
        }
    }

    /**
     * 根据当前请求（URL+方法）获取所需权限（核心修复）
     */
    @Override
//    getAttributes： 根据传入的请求对象，返回该请求所需的权限集合
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        // 增强健壮性：确保permissionMap不为null
        if (permissionMap == null || permissionMap.isEmpty()) {
            logger.warn("Permission map is null or empty, reloading permissions");
            loadPermissionMap(); // 重新加载权限
        }

        FilterInvocation fi = (FilterInvocation) object;
        HttpServletRequest request = fi.getHttpRequest();
        // 增强健壮性：确保request不为null
        if (request == null) {
            logger.warn("HttpServletRequest is null");
            return SecurityConfig.createList("ROLE_LOGIN");
        }

        String requestUrl = request.getRequestURI();
        // 增强健壮性：确保requestUrl不为null
        if (requestUrl == null) {
            logger.warn("Request URI is null");
            return SecurityConfig.createList("ROLE_LOGIN");
        }

        // 定义公开路径（与SecurityConfiguration中的permitAll()保持一致）
        // 这些路径不需要权限验证，返回空集合
        String[] publicPaths = {
            "/toLoginPage", "/login", "/ershouproducts", "/register", "/doRegister",
            "/addToCart", "/updateCart", "/removeFromCart", "/cart"
        };
        
        // 需要登录但由SecurityConfiguration控制角色权限的路径
        // 这些路径返回空集合，让SecurityConfiguration的hasAnyRole配置生效
        String[] roleBasedPaths = {
            "/ProductsFront", "/front/homepage"
        };
        
        requestUrl = requestUrl.replaceAll("/$", ""); // 去除尾斜杠，统一格式
        AntPathMatcher pathMatcher = new AntPathMatcher();
        
        // 检查是否为公开路径，如果是则返回空集合（表示不需要权限验证）
        for (String publicPath : publicPaths) {
            if (pathMatcher.match(publicPath, requestUrl)) {
                return new ArrayList<>(); // 返回空集合，表示不需要权限验证
            }
        }
        
        // 检查是否为基于角色的路径，返回空集合让SecurityConfiguration的hasAnyRole配置生效
        for (String roleBasedPath : roleBasedPaths) {
            if (pathMatcher.match(roleBasedPath, requestUrl)) {
                return new ArrayList<>(); // 返回空集合，让SecurityConfiguration的配置生效
            }
        }

        String requestMethod = request.getMethod();
        // 增强健壮性：确保requestMethod不为null
        if (requestMethod == null) {
            logger.warn("Request method is null");
            return SecurityConfig.createList("ROLE_LOGIN");
        }

        requestMethod = requestMethod.toUpperCase(); // 获取请求方法（GET/POST等）

        // 1. 优先精确匹配"请求方式:URL"（如"GET:/users"）
        String exactKey = requestMethod + ":" + requestUrl;
        if (permissionMap.containsKey(exactKey)) {
            return permissionMap.get(exactKey);
        }

        // 2. 通配符匹配（如"/users/**"），同时校验请求方式
        for (String patternKey : permissionMap.keySet()) {
            // 增强健壮性：检查patternKey是否为null
            if (patternKey == null) {
                continue;
            }

            String[] keyParts = patternKey.split(":", 2);
            if (keyParts.length != 2) {
                continue;
            }
            String patternMethod = keyParts[0]; // 权限配置的方法（*或具体方法）
            String patternUrl = keyParts[1]; // 权限配置的URL（含通配符）

            // 方法匹配：*匹配所有，否则精确一致
            boolean methodMatch = "*".equals(patternMethod) || patternMethod.equals(requestMethod);
            // URL匹配：Ant风格通配符匹配
            boolean urlMatch = pathMatcher.match(patternUrl, requestUrl);

            if (methodMatch && urlMatch) {
                return permissionMap.get(patternKey);
            }
        }

        // 3. 未匹配到任何权限：默认需要登录
        return SecurityConfig.createList("ROLE_LOGIN");
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        // 增强健壮性：确保permissionMap不为null
        if (permissionMap == null) {
            return new HashSet<>();
        }

        // 返回所有权限（框架启动时校验用）
        return permissionMap.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
}