package edu.uclm.esi.fakeaccountsbe.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import edu.uclm.esi.fakeaccountsbe.security.TokenAuthenticationFilter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private TokenAuthenticationFilter tokenAuthenticationFilter;

    @Bean // Registrar el filtro en el contenedor de Spring
    public FilterRegistrationBean<TokenAuthenticationFilter> loggingFilter(){
        FilterRegistrationBean<TokenAuthenticationFilter> registrationBean 
          = new FilterRegistrationBean<>();

        registrationBean.setFilter(tokenAuthenticationFilter);
        registrationBean.addUrlPatterns("/*"); // Aplicar a todas las rutas

        return registrationBean;    
    }
}
