package com.example.rule;

import cn.hutool.core.io.FileUtil;
import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class RuleExecutor {
    final static Gson gson = new GsonBuilder().create();

    List<RuleElement> trees;//规则集 必须全部为tree
    Map<String, Objects> context;//执行上下文
    Map<String, Objects> types;//执行上下文 对应的类型


    public RuleExecutor(List<RuleElement> trees) {
        this.trees = trees;
        isValid();
    }

    private Boolean isValid() {
        Preconditions.checkArgument(trees != null, "input trees is null");
        System.out.println("trees:" + trees);
        System.out.println("trees:" + trees.size());
        boolean checkTree = trees.stream().map(RuleElement::isTree).allMatch(element -> Boolean.TRUE.equals(element));
        System.out.println("checkTree" + checkTree);
        Preconditions.checkArgument(checkTree != false, "checkTree false");

        return Boolean.TRUE;
    }


    public static void main(String[] args) {
        String line = FileUtil.readString("test.json", Charset.defaultCharset());
        List<RuleElement> trees = gson.fromJson(line, new TypeToken<List<RuleElement>>() {
        }.getType());

        RuleExecutor executor = new RuleExecutor(trees);
        //从树形结构获取所有规则 以叶子节点为尾
        List<List<RuleElement>> res = RuleElement.getRuleListFromTree(trees);
        System.out.println("========");
        System.out.println("res:" + res.size());
        res.forEach(ruleElements -> {
            System.out.println("ruleElements:" + ruleElements.size());
            System.out.println("ruleElements data:" + ruleElements.stream().map(RuleElement::getId).collect(Collectors.toList()));
        });
        System.out.println("========");

        //executor对每一个输入进行执行

        //执行的结果
    }
}
