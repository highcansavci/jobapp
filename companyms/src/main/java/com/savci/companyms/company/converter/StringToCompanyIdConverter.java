package com.savci.companyms.company.converter;

import com.savci.companyms.company.entity.CompanyId;
import com.savci.companyms.company.service.CompanyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

public class StringToCompanyIdConverter implements Converter<String, CompanyId> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyService.class);

    @Override
    public CompanyId convert(String source) {
        try {
            return new CompanyId(Long.parseLong(source));
        } catch(NumberFormatException e) {
            return new CompanyId(null);
        }
    }
}
