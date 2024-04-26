package com.savci.reviewms.review.converter;

import com.savci.reviewms.review.entity.CompanyId;
import org.springframework.core.convert.converter.Converter;

public class StringToCompanyIdConverter implements Converter<String, CompanyId> {

    @Override
    public CompanyId convert(String source) {
        try {
            return new CompanyId(Long.parseLong(source));
        } catch(NumberFormatException e) {
            return new CompanyId(null);
        }
    }

}
