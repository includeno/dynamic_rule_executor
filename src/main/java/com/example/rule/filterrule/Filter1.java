package com.example.rule.filterrule;

import com.example.rule.RuleContext;
import com.example.rule.RuleOperation;

import java.util.HashMap;
import java.util.Map;

//指定类型过滤器，要求RuleContext存在指定Key的数据
public class Filter1 extends AbstractFilter {
    //定义必须存在的上下文数据
    private static final Map<RuleContext.ContextKey, String> keys = new HashMap<>();

    static {
        keys.put(RuleContext.ContextKey.AMOYUNT, RuleContext.ContextKey.AMOYUNT.getDes());
    }

    public Filter1(RuleContext ruleContext) {
        super(ruleContext, keys);
    }

    public Boolean filter1(String operation, Double amount) {
        ruleContext.getContextDataMap().forEach((key, value) -> {
            System.out.println(key);
            System.out.println(value);
        });

        if (Boolean.TRUE.equals(RuleOperation.operation(operation, (Double) getData(RuleContext.ContextKey.AMOYUNT).getValue(), amount))) {
            return true;
        }
        return false;
    }
}
