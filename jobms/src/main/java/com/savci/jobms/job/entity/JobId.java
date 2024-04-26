package com.savci.jobms.job.entity;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JobId {
    @Positive(message = "The job id should be positive.")
    @NotNull(message = "The job id should not be null.")
    private Long id;
}
