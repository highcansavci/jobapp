package com.savci.jobms.job.dto;

import com.savci.jobms.job.external.Company;
import com.savci.jobms.job.entity.Job;
import com.savci.jobms.job.external.Review;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder(access = AccessLevel.PUBLIC)
public class JobDTO {
    @EqualsAndHashCode.Exclude
    private Long id;
    private String name;
    private String description;
    private String minSalary;
    private String maxSalary;
    private String location;
    @Valid
    @NotNull(message = "The company should not be null.")
    private Company company;
    private List<Review> reviews;

    public static JobDTO createJobDTO(Job job, Company company, List<Review> reviews) {
        return JobDTO.builder()
                .id(job.getId())
                .name(job.getName())
                .description(job.getDescription())
                .minSalary(job.getMinSalary())
                .maxSalary(job.getMaxSalary())
                .location(job.getLocation())
                .company(company)
                .reviews(reviews)
                .build();
    }
}
