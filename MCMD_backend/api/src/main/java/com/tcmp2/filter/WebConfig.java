package com.tcmp2.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;
/**
 * @author : Zhang Chengwei
 * @createDate : 2025-03-02 19:53
 * @description :Web配置类
 * 用于注册过滤器和其他Web相关配置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private RateLimitInterceptor rateLimitInterceptor;
    
    @Value("${cors.allowed-origins:http://localhost:8080}")
    private String allowedOriginsString;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册速率限制拦截器
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/**"); // 应用到所有路径
    }

    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtFilter(JwtAuthFilter filter) {
        FilterRegistrationBean<JwtAuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns("/materials/*", "/search/*", "/update_material/*", "/add_material", "/delete_material/*", "/delete_all_materials", "/data-count", "/test_mongodb_connection");  // 添加需要保护的URL模式
        registrationBean.setOrder(1);  // 设置过滤器顺序
        return registrationBean;
    }
    
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // 允许携带cookie
        
        // 从环境变量或配置文件中获取允许的源，默认为localhost:8080
        List<String> allowedOrigins = Arrays.asList(allowedOriginsString.split(","));
        allowedOrigins.forEach(config::addAllowedOrigin);
        
        // 限制允许的HTTP方法
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("OPTIONS");
        
        // 限制允许的请求头
        config.addAllowedHeader("Authorization");
        config.addAllowedHeader("Content-Type");
        config.addAllowedHeader("Accept");
        
        // 允许前端访问的响应头
        config.addExposedHeader("Set-Cookie");
        
        // 设置预检请求的缓存时间（秒）
        config.setMaxAge(3600L);
        
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(0);  // 确保CORS过滤器在JWT过滤器之前执行
        return bean;
    }
} 