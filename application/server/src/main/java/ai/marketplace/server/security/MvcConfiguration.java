package ai.marketplace.server.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


//serves client as static content
@Configuration
public class MvcConfiguration{
    @Bean
    public WebMvcConfigurer forwardToIndex() {
        return new WebMvcConfigurer() {
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                // forward requests to /admin and /user to their index.html
                registry.addViewController("/").setViewName("redirect:/index.html");
                registry.addViewController("/login/**").setViewName("forward:/index.html");
                registry.addViewController("/search").setViewName("forward:/index.html");
                registry.addViewController("/registration").setViewName("forward:/index.html");
                registry.addViewController("/home").setViewName("forward:/index.html");
            }
        };
    }

}
