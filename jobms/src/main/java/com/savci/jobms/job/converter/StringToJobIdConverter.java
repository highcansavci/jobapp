package com.savci.jobms.job.converter;

import com.savci.jobms.job.entity.JobId;
import org.springframework.core.convert.converter.Converter;

public class StringToJobIdConverter implements Converter<String, JobId> {

    @Override
    public JobId convert(String source) {
        try {
            return new JobId(Long.parseLong(source));
        } catch(NumberFormatException e) {
            return new JobId(null);
        }
    }
}
