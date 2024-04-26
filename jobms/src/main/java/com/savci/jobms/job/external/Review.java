package com.savci.jobms.job.external;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

@JsonDeserialize(builder = Review.ReviewBuilder.class)
@Value
@Builder(access = AccessLevel.PUBLIC)
public class Review {
    Long id;
    String title;
    String description;
    Double rating;
    Long companyId;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ReviewBuilder {

    }
}
