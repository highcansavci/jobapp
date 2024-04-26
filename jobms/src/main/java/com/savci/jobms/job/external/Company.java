package com.savci.jobms.job.external;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.*;

@JsonDeserialize(builder = Company.CompanyBuilder.class)
@Value
@Builder(access = AccessLevel.PUBLIC)
public class Company {
    Long id;
    String name;
    String description;

    @JsonPOJOBuilder(withPrefix = "")
    public static class CompanyBuilder {

    }
}
