package org.example.finalproject.demos.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

//安全配置（URL权限、登录/注销、密码加密）
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    // 注入自定义用户详情服务（查询用户信息）
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    // 注入自定义权限元数据源（获取URL所需权限）
    @Autowired
    private CustomFilterInvocationSecurityMetadataSource securityMetadataSource;
    // 注入自定义权限决策管理器（判断用户是否有权限访问）
    @Autowired
    private CustomUrlDecisionManager urlDecisionManager;
    // 注入自定义登录成功处理器
    @Autowired
    private CustomAuthenticationSuccessHandler authenticationSuccessHandler;
    // 密码加密器（BCrypt算法）
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    // 配置静态资源放行（不经过Security过滤器链）
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/css/**", "/js/**", "/images/**"); // 静态资源路径，根据实际项目调整
    }
    // 核心安全配置（URL拦截、登录、权限控制等）
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // 1. 禁用CSRF保护（针对API端点，允许POST请求）
        http.csrf().ignoringAntMatchers(
                "/addToCart",
                "/updateCart",
                "/removeFromCart",
                "/order/buyNow",
                "/order/checkoutCart",
                "/login",
                "/doRegister",
                "/pay/alipay/notify",
                "/api/chat/**"
        );

        // 2. 配置URL访问权限
        http.authorizeRequests()
                // 公开路径（所有用户可访问，包括未登录用户）
                .antMatchers(
                    "/toLoginPage", 
                    "/login",
                    "/register", 
                    "/doRegister",
                    "/ershouproducts",        // 商品列表（公开）
                    "/productDetail",        // 商品详情（公开）
                    "/searchProducts",       // 搜索商品（公开）
                    "/productsCategory",     // 分类商品（公开）
                    "/pay/alipay/notify",    // 支付异步回调
                    "/pay/alipay/return"     // 支付同步回跳
                ).permitAll()
                
                // 管理员路径
                .antMatchers("/admin/**").hasRole("ADMIN")
                
                // 卖家路径（卖家和管理员都可以访问）
                .antMatchers("/seller/**").hasAnyRole("SELLER", "ADMIN")
                
                // 买家路径（买家和管理员都可以访问）
                .antMatchers("/buyer/**").hasAnyRole("BUYER", "USER", "ADMIN")
                
                // 现有的买家页面路径（需要登录，但不限制角色）
                .antMatchers("/ProductsFront", "/front/homepage", "/cart", "/addToCart", "/updateCart", "/removeFromCart", "/order/buyNow", "/order/checkoutCart", "/pay/alipay/page")
                    .hasAnyRole("BUYER", "USER", "SELLER", "ADMIN")
                
                // 其他所有请求必须认证（登录后才能访问）
                .anyRequest().authenticated()
                // 3. 绑定自定义权限控制（URL权限匹配+决策）
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O object) {
                        // 设置权限元数据源（URL→所需权限的映射）
                        object.setSecurityMetadataSource(securityMetadataSource);
                        // 设置权限决策管理器（判断用户是否有权限）
                        object.setAccessDecisionManager(urlDecisionManager);
                        return object;
                    }
                })
                .and()

                // 4. 配置表单登录（自定义登录页和提交路径）
                .formLogin()
                .loginPage("/toLoginPage") // 登录页路径（未登录时自动跳转这里）
                .loginProcessingUrl("/login") // 登录提交路径（表单action指向此路径）
                // 使用自定义登录成功处理器（根据角色跳转）
                .successHandler(authenticationSuccessHandler)
                .failureUrl("/toLoginPage?error=true") // 登录失败后跳转的页面（带错误参数）
                .usernameParameter("username") // 登录账号参数名（与表单name一致）
                .passwordParameter("password") // 登录密码参数名（与表单name一致）
                .permitAll() // 允许所有用户访问登录相关路径
                .and()

                // 5. 配置注销功能
                .logout()
                .logoutUrl("/logout") // 注销请求路径
                .logoutSuccessUrl("/toLoginPage?logout=true") // 注销成功后跳转登录页
                .invalidateHttpSession(true) // 注销时销毁session
                .clearAuthentication(true) // 清除认证信息
                .permitAll()
                .and()

                // 6. 配置权限不足处理（403页面）
                .exceptionHandling()
                .accessDeniedPage("/403"); // 权限不足时跳转的页面
    }
    // 配置认证管理器
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService)
            .passwordEncoder(passwordEncoder());
    }
}
