package com.savci.reviewms.review.messaging;

import com.savci.reviewms.review.dto.ReviewMessage;
import com.savci.reviewms.review.entity.Review;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ReviewMessageProducer {
    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(Review review) {
        ReviewMessage reviewMessage = new ReviewMessage(review);
        rabbitTemplate.convertAndSend("companyRatingQueue", reviewMessage);
    }
}
