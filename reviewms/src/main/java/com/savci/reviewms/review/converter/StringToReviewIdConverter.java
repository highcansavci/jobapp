package com.savci.reviewms.review.converter;

import com.savci.reviewms.review.entity.ReviewId;
import org.springframework.core.convert.converter.Converter;

public class StringToReviewIdConverter implements Converter<String, ReviewId> {

    @Override
    public ReviewId convert(String source) {
        try {
            return new ReviewId(Long.parseLong(source));
        } catch(NumberFormatException e) {
            return new ReviewId(null);
        }
    }
}
