package com.example.rule.filterrule;

import cn.hutool.core.io.FileUtil;
import com.example.entity.RuleEntity;
import com.example.rule.RuleContext;
import com.example.rule.RuleContextData;
import com.example.rule.RuleElement;
import com.example.rule.RuleExecutor;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Filter2 extends AbstractFilter<RuleContext.ContextKey> {
    //定义必须存在的上下文数据
    private static final Map<RuleContext.ContextKey, String> keys = new HashMap<>();
    private static final Gson gson = new Gson();

    static {
        keys.put(RuleContext.ContextKey.AMOYUNT, RuleContext.ContextKey.AMOYUNT.getDes());
        keys.put(RuleContext.ContextKey.WHITE_LIST, RuleContext.ContextKey.WHITE_LIST.getDes());
    }

    private RuleContext ruleContext;

    public Filter2(RuleContext ruleContext) {
        super(ruleContext);
        this.ruleContext = ruleContext;
    }

    @Override
    public Map<RuleContext.ContextKey, String> getKeys() {
        return keys;
    }

    public Boolean filter2(List<String> whiteList, Double amount) {
        System.out.println("filter2");
        if (whiteList.contains("name5")) {
            return false;
        }
        System.out.println(amount);
        return true;
    }

    public static void main(String[] args) {
        //步骤1 定义上下文数据
        RuleContext ruleContext = new RuleContext();
        ruleContext.put(new RuleContextData(RuleContext.ContextKey.AMOYUNT, 3.0, Double.class));
        ruleContext.put(new RuleContextData(RuleContext.ContextKey.WHITE_LIST, Lists.newArrayList("name1", "name2", "name3"), TypeToken.getParameterized(List.class, String.class).getType()));
        //步骤2 定义规则
        RuleEntity ruleEntity = new RuleEntity();
        ruleEntity.setKey("Filter2");
        ruleEntity.setDes("执行Filter2");
        Map<String, Object> map = Maps.newHashMap();
        //定义在方法内部的参数
        map.put("white_list", Lists.newArrayList("name1", "name2", "name4"));
        map.put("amount", 2);
        ruleEntity.setValue(new Gson().toJson(map));

        //步骤3 根据上下文和规则
        System.out.println("开始执行规则" + LocalDateTime.now());
        FilterRuleFunction.valueOf(ruleEntity.getKey()).executeFilter(ruleContext, ruleEntity);
        System.out.println("结束执行规则" + LocalDateTime.now());

        //步骤4 生成表格
        String line = FileUtil.readString("test.json", Charset.defaultCharset());
        List<RuleElement> trees = gson.fromJson(line, new TypeToken<List<RuleElement>>() {
        }.getType());

        RuleExecutor executor = new RuleExecutor(trees);
    }
}
