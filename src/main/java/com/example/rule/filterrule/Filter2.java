package com.example.rule.filterrule;

import com.example.rule.RuleContext;

import java.util.List;

public class Filter2 {
    private RuleContext ruleContext;

    public Filter2(RuleContext ruleContext) {
        this.ruleContext = ruleContext;
    }

    public Boolean filter2(List<String> whiteList, Double amount) {
        System.out.println("filter2");
        if(whiteList.contains("name1")){
            return false;
        }
        System.out.println(amount);
        return true;
    }
}
