package com.example.rule.filterrule;

import com.example.rule.RuleContext;
import com.example.rule.RuleContextData;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Data
public abstract class AbstractFilter<T extends Enum> {
    RuleContext ruleContext;

    public AbstractFilter(RuleContext ruleContext) {
        this.ruleContext = ruleContext;
        Map<T, String> keys = getKeys();
        if(keys == null){
            return;
        }
        //检查是否存在指定的Key
        for (T contextKey : keys.keySet()) {
            if (!ruleContext.getContextDataMap().containsKey(contextKey.name())) {
                throw new RuntimeException("不存在指定的Key" + contextKey.name());
            }
        }
    }

    public abstract Map<T, String> getKeys();

    public RuleContextData getData(RuleContext.ContextKey contextKey) {
        return ruleContext.getContextDataMap().get(contextKey.name());
    }
}
