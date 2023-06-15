package com.example.rule;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Type;

@Data
@AllArgsConstructor
public class RuleParameter {
    private Type type;
    private String des;
    private String name;
}
