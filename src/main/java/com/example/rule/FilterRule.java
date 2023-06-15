package com.example.rule;

import com.example.entity.RuleEntity;

public interface FilterRule {
    Boolean executeFilter(RuleContext context, RuleEntity ruleEntity);

}
