package com.example.rule.filterrule;

import com.example.rule.RuleContext;
import com.example.rule.RuleContextData;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Data
public class AbstractFilter {
    RuleContext ruleContext;

    public AbstractFilter(RuleContext ruleContext, Map<RuleContext.ContextKey, String> keys) {
        this.ruleContext = ruleContext;
        //检查是否存在指定的Key
        for (RuleContext.ContextKey contextKey : keys.keySet()) {
            if (!ruleContext.getContextDataMap().containsKey(contextKey.name())) {
                throw new RuntimeException("不存在指定的Key" + contextKey.name());
            }
        }
    }

    public RuleContextData getData(RuleContext.ContextKey contextKey) {
        return ruleContext.getContextDataMap().get(contextKey.name());
    }
}
