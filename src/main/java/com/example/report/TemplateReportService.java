package com.example.report;

import freemarker.template.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class TemplateReportService {

    private static Configuration configuration;

    @Resource
    public void setConfiguration(Configuration configuration) {
        TemplateReportService.configuration = configuration;
    }


}
