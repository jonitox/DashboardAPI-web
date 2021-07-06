package com.kakao.test1.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class FreemarkerConfig {
    @Autowired
    private FreeMarkerProperties freeMarkerProperties;

    @Bean
    public FreeMarkerConfigurer test() {
        FreeMarkerConfigurer freemarkerConfig = new FreeMarkerConfigurer();
        freemarkerConfig.setTemplateLoaderPath("classpath:/templates");
        freemarkerConfig.setDefaultEncoding("UTF-8");
        Properties properties = new Properties();
        properties.putAll(freeMarkerProperties.getSettings());
        freemarkerConfig.setFreemarkerSettings(properties);

        Map<String, Object> freemarkerVariables = new HashMap<>();
        freemarkerConfig.setFreemarkerVariables(freemarkerVariables);
        return freemarkerConfig;
    }
}
