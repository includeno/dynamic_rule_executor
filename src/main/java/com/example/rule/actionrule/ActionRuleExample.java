package com.example.rule.actionrule;

import com.example.rule.ActionRule;

import java.util.Map;
import java.util.function.Function;

public enum ActionRuleExample implements ActionRule {
    A("a", Integer.class, (input) -> Integer.valueOf(String.valueOf(input)).intValue() > 0),
    ;

    final String des;
    final Class clazz;
    Function<? super Object, ?> mapper;

    ActionRuleExample(String des, Class clazz, Function<? super Object, ?> mapper) {
        this.des = des;
        this.clazz = clazz;
        this.mapper = mapper;
    }


    public static void main(String[] args) {

    }

    @Override
    public void executeAction(Map<String, Object> parameter) {

    }
}
