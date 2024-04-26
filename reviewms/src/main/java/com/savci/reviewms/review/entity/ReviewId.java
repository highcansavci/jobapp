package com.savci.reviewms.review.entity;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReviewId {
    @Positive(message = "The review id should be positive.")
    @NotNull(message = "The review id should not be null.")
    private Long id;
}
