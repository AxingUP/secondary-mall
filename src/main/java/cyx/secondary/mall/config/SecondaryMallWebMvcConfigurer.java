package cyx.secondary.mall.config;


import cyx.secondary.mall.interceptor.AdminLoginInterceptor;
import cyx.secondary.mall.common.Constants;
import cyx.secondary.mall.interceptor.SecondaryMallCartNumberInterceptor;
import cyx.secondary.mall.interceptor.SecondaryMallLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecondaryMallWebMvcConfigurer implements WebMvcConfigurer {

    @Autowired
    private AdminLoginInterceptor adminLoginInterceptor;

    @Autowired
    private SecondaryMallLoginInterceptor secondaryMallLoginInterceptor;

    @Autowired
    private SecondaryMallCartNumberInterceptor secondaryMallCartNumberInterceptor;

    public void addInterceptors(InterceptorRegistry registry) {
//         添加一个拦截器，拦截以/admin为前缀的url路径（后台登陆拦截）
//                由于后端管理系统的所有请求路径都以 /admin 开头
//                所以拦截的路径为 /admin/**
//                但是登陆页面以及部分静态资源文件也是以 /admin 开头
//                所以需要将这些路径排除，配置如上
//                 addPathPatterns() 方法和 excludePathPatterns() 两个方法
//                 分别是添加路径拦截规则和排除路径拦截规则。
        registry.addInterceptor(adminLoginInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/login")
                .excludePathPatterns("/admin/dist/**")
                .excludePathPatterns("/admin/upload/**")
                .excludePathPatterns("/admin/plugins/**")
                .excludePathPatterns("admin/goods");
        // 商城页面登陆拦截
        registry.addInterceptor(secondaryMallLoginInterceptor)
                .excludePathPatterns("/mall/**")
                .excludePathPatterns("/admin/**")
                .excludePathPatterns("/register")
                .excludePathPatterns("/login")
                .excludePathPatterns("/logout")
                .addPathPatterns("/goods/detail/**")
                .addPathPatterns("/shop-cart")
                .addPathPatterns("/shop-cart/**")
                .addPathPatterns("/saveOrder")
                .addPathPatterns("/orders")
                .addPathPatterns("/orders/**")
                .addPathPatterns("/personal")
                .addPathPatterns("/personal/updateInfo")
                .addPathPatterns("/orders/**")
                .addPathPatterns("/selectPayType")
                .addPathPatterns("/payPage")
                .addPathPatterns("/mall/my-sells")
                .addPathPatterns("/mall/mysell");
        // 购物车中的数量统一处理
        registry.addInterceptor(secondaryMallCartNumberInterceptor)
                .excludePathPatterns("/admin/**")
                .excludePathPatterns("/register")
                .excludePathPatterns("/login")
                .excludePathPatterns("/logout");
    }

//    文件上传路径回显
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/upload/**").addResourceLocations("file:" + Constants.FILE_UPLOAD_DIC);
        registry.addResourceHandler("/goods-img/**").addResourceLocations("file:" + Constants.FILE_UPLOAD_DIC);
    }
}
