package com.savci.companyms.company.converter;

import com.savci.companyms.company.service.CompanyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class IdConfig implements WebMvcConfigurer {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdConfig.class);
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToCompanyIdConverter());
    }
}
