package com.example.rule;

import lombok.Data;

@Data
public class RuleContextData {
    private String key;
    private Object value;
    private Class type;

    public RuleContextData(String key, Object value, Class type) {
        this.key = key;
        this.value = value;
        this.type = type;
    }

    public RuleContextData(RuleContext.ContextKey key, Object value, Class type) {
        this.key = key.name();
        this.value = value;
        this.type = type;
    }
}
