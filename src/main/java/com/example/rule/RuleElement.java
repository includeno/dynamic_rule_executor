package com.example.rule;

import cn.hutool.core.io.FileUtil;
import com.example.entity.RuleEntity;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * 规则基本元素 可以嵌套
 */
public class RuleElement extends RuleEntity {
    final static Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
        @Override
        public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            Instant instant = Instant.ofEpochMilli(json.getAsJsonPrimitive().getAsLong());
            return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        }
    }).create();

    List<RuleElement> contents;//嵌套内容

    public Boolean isTree() {
        return getContents() != null && getContents().size() > 0;
    }

    //仅复制属性信息，不复制嵌套结构
    public RuleElement copyNode(RuleElement obj) {
        RuleElement ruleElement = new RuleElement();
        ruleElement.setKey(obj.getKey());
        ruleElement.setValue(obj.getValue());
        ruleElement.setOperation(obj.getOperation());
        ruleElement.setDes(obj.getDes());
        ruleElement.setSortIndex(obj.getSortIndex());
        return ruleElement;
    }

    /**
     * 树形结构转为实体类型
     */
    public static RuleEntity toRuleEntity(RuleElement element) {
        RuleEntity ruleEntity = new RuleEntity();
        ruleEntity.setUuid(element.getUuid());
        ruleEntity.setParentId(element.getParentId());
        ruleEntity.setRootId(element.getRootId());
        ruleEntity.setKey(element.getKey());
        ruleEntity.setValue(element.getValue());
        ruleEntity.setOperation(element.getOperation());
        ruleEntity.setDes(element.getDes());
        ruleEntity.setSortIndex(element.getSortIndex());
        ruleEntity.setCreateTime(LocalDateTime.now());
        ruleEntity.setUpdateTime(LocalDateTime.now());
        ruleEntity.setValid(1);
        return ruleEntity;
    }


    //RuleElement和RuleEntity转换
    public static List<RuleEntity> toNewRuleEntityList(List<RuleElement> ruleElements) {
        List<RuleElement> trees = getNodes(ruleElements);//将元素以树状形式组合
        log.info("first trees:" + new Gson().toJson(trees));
        //设置sortIndex
        assignSortIndex(trees);
        trees.sort(elementComparator);
        //转换为RuleEntity
        return trees.stream().map(element -> toRuleEntity(element)).collect(Collectors.toList());
    }

    public static List<RuleElement> getRuleElementListFromTree(List<RuleElement> elements) {
        if (elements == null || elements.isEmpty() || Boolean.FALSE.equals(elements.get(0).isTree())) {
            return elements;
        }
        List<RuleElement> list = new ArrayList<>();//数组形式的元素列表
        for (int i = 0; i < elements.size(); i++) {
            RuleElement child = elements.get(i);
            dfsLevel(child, list);
        }
        list.sort(elementComparator);
        list.forEach(element -> {
            element.setContents(null);//清空嵌套内容
        });
        return list;
    }

    public static void dfsLevel(RuleElement element, List<RuleElement> ans) {
        ans.add(element);
        if (element.getContents() != null) {
            for (int i = 0; i < element.getContents().size(); i++) {
                RuleElement child = element.getContents().get(i);
                dfsLevel(child, ans);
            }
        }
    }

    //RuleElement和RuleEntity转换
    public static List<RuleElement> toRuleElement(List<RuleEntity> ruleEntities) {
        return null;
    }

    public static Comparator<RuleElement> elementComparator = new Comparator<RuleElement>() {
        @Override
        public int compare(RuleElement o1, RuleElement o2) {
            return o1.getSortIndex().compareTo(o2.getSortIndex());
        }
    };

    //遍历所有元素
    public static List<RuleElement> getNodes(List<RuleElement> elements) {
        List<RuleElement> allElements = new ArrayList<>();
        for (RuleElement element : elements) {
            collectAllNodes(element, allElements);
        }
        return allElements.stream().sorted(elementComparator).collect(Collectors.toList());
    }

    private static void collectAllNodes(RuleElement element, List<RuleElement> result) {
        if (result == null) {
            result = new ArrayList<>();
        }
        result.add(element);
        if (element.getContents() != null) {
            for (RuleElement child : element.getContents()) {
                System.out.println("child:" + child.getId());
                collectAllNodes(child, result);
            }
        }
    }

    //在树形结构中设置sortIndex
    private static void assignSortIndex(List<RuleElement> elements) {
        double globalSortIndex = 0;
        for (RuleElement element : elements) {
            element.setSortIndex(BigDecimal.valueOf(++globalSortIndex));
        }
    }

    //从树形结构获取所有规则 以叶子节点为尾
    public static List<List<RuleElement>> getRuleListFromTree(List<RuleElement> elements) {
        List<List<RuleElement>> res = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            List<List<RuleElement>> ans = new ArrayList<>();
            RuleElement element = elements.get(i);
            List<RuleElement> list = new ArrayList<>();
            pathDfs(element, list, ans);
            res.addAll(ans);
        }
        return res;
    }

    private static void pathDfs(RuleElement element, List<RuleElement> temp, List<List<RuleElement>> ans) {
        temp.add(element);
        if (element.getContents() == null || element.getContents().isEmpty()) {
            ans.add(new ArrayList<>(temp));
            return;
        }
        for (int i = 0; i < element.getContents().size(); i++) {
            RuleElement child = element.getContents().get(i);
            pathDfs(child, temp, ans);
            temp.remove(temp.size() - 1);
        }
    }

    public static void main(String[] args) {
        //j读取test.json文件的字符串格式的树形结构
        //String json=
        String line = FileUtil.readString("test.json", Charset.defaultCharset());
        List<RuleElement> elementList = gson.fromJson(line, new TypeToken<List<RuleElement>>() {
        }.getType());
        System.out.println("==================================");
        System.out.println("origin:" + gson.toJson(elementList));
        System.out.println("==================================");
        List<RuleElement> trees = getNodes(elementList);
        trees.forEach(st -> System.out.println("tree:" + gson.toJson(st)));
        System.out.println("==================================");
        List<RuleEntity> list = toNewRuleEntityList(elementList);
        list.forEach(st -> System.out.println("final list:" + gson.toJson(st)));

    }
}