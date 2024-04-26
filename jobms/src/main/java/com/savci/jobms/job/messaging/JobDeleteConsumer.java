package com.savci.jobms.job.messaging;

import com.savci.jobms.job.service.JobService;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class JobDeleteConsumer {
    private final JobService jobService;

    @RabbitListener(queues = "companyJobDeleteQueue")
    public void consumeMessage(Long companyId) {
        jobService.deleteJobByCompanyId(companyId);
    }
}
