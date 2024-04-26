package com.savci.reviewms.review.entity;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class CompanyId {
    @Positive(message = "The company id should be positive.")
    @NotNull(message = "The company id should not be null.")
    private Long id;
}
