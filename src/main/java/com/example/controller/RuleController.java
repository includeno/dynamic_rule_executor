package com.example.controller;

import com.example.rule.RuleOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class RuleController {
    //获取所有当前数据允许的操作符
    @GetMapping("/operation")
    public List<String> operation(Object data) {
        return RuleOperation.getOperationsByType(data.getClass());
    }


}
