package com.savci.reviewms.review.messaging;

import com.savci.reviewms.review.service.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ReviewDeleteComsumer {
    private final ReviewService reviewService;

    @RabbitListener(queues = "companyReviewDeleteQueue")
    public void consumeMessage(Long companyId) {
        reviewService.deleteReviewByCompanyId(companyId);
    }
}
