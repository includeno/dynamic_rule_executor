package com.example.rule;

import com.example.utils.TypeUtils;
import com.google.gson.Gson;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

//上下文数据
//包括执行方法所需的各种参数
@Getter
public class RuleContext {
    private static Gson gson = new Gson();

    @Getter
    public enum ContextKey {
        AMOYUNT(Double.class, "金额"),
        WHITE_LIST(String.class, "白名单"),
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
        if (parameter == null) {
            throw new RuntimeException("参数不能为空");
        }
        System.out.println("parameter.getType():" + parameter.getType().getTypeName());
        System.out.println("parameter.getValue():" + parameter.getValue().getClass().getName());
        System.out.println("parameter.getType().getClass():" + parameter.getType());
        TypeUtils.typeEquals(new RuleParameter(parameter.getType(), "", ""), parameter.getValue());
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
