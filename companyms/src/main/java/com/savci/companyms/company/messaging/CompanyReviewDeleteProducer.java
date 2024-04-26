package com.savci.companyms.company.messaging;

import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CompanyReviewDeleteProducer {
    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(Long companyId) {
        rabbitTemplate.convertAndSend("companyReviewDeleteQueue", companyId);
    }
}
