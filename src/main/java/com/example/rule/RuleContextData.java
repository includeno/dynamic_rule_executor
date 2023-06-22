package com.example.rule;

import lombok.Data;

import java.lang.reflect.Type;

@Data
public class RuleContextData {
    private String key;
    private Object value;
    private Type type;

    public RuleContextData(String key, Object value, Type type) {
        this.key = key;
        this.value = value;
        this.type = type;
    }

    public RuleContextData(RuleContext.ContextKey key, Object value, Type type) {
        this.key = key.name();
        this.value = value;
        this.type = type;
    }
}
