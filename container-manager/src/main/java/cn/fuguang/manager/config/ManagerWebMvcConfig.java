package cn.fuguang.manager.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class ManagerWebMvcConfig implements WebMvcConfigurer {

    @Resource
    private ManagerPermissionInterceptor managerPermissionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 2026-05-31: manager 后端接口级权限校验，前端按钮控制不能作为唯一防线。
        registry.addInterceptor(managerPermissionInterceptor)
                .addPathPatterns("/api/manager/**");
    }
}
