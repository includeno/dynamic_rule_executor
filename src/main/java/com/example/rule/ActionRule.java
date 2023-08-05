package com.example.rule;

import java.util.Map;

public interface ActionRule {
    void executeAction(Map<String, Object> parameter);
}
