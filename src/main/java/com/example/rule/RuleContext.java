package com.example.rule;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

//上下文数据
//包括执行方法所需的各种参数
@Getter
public class RuleContext {
    @Getter
    public enum ContextKey {
        AMOYUNT(Double.class, "金额"),
        ;

        Class<?> type;
        String des;

        ContextKey(Class<?> type, String des) {
            this.type = type;
            this.des = des;
        }
    }

    Map<String, RuleContextData> contextDataMap = new HashMap<>();

    public String getKey(String name) {
        return name;
    }

    public void put(RuleContextData parameter) {
        if (!parameter.getType().isInstance(parameter.getValue())) {
            throw new RuntimeException("类型不匹配");
        }
        String key = getKey(parameter.getKey());
        //方法名称重复
        if (contextDataMap.containsKey(key)) {
            throw new RuntimeException("名称重复");
        }
        contextDataMap.put(key, parameter);
    }

    public void put(String name, Object value, Class type) {
        RuleContextData ruleContextData = new RuleContextData(name, value, type);
        put(ruleContextData);
    }

    public void put(ContextKey name, Object value, Class type) {
        RuleContextData ruleContextData = new RuleContextData(name, value, type);
        put(ruleContextData);
    }

    public static void main(String[] args) {
        RuleContext ruleContext = new RuleContext();
        ruleContext.put(ContextKey.AMOYUNT, "1", String.class);

    }
}
