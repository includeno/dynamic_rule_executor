package com.example.rule.filterrule;

import com.example.entity.RuleEntity;
import com.example.rule.FilterRule;
import com.example.rule.RuleContext;
import com.example.rule.RuleContextData;
import com.example.rule.RuleParameter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.*;
import java.util.*;

@Slf4j
public enum FilterRuleFunction implements FilterRule {
    Fileter1("filter1",
            Lists.newArrayList(
                    new RuleParameter(new TypeToken<String>() {
                    }.getType(), "操作符", "operation"),
                    new RuleParameter(new TypeToken<Double>() {
                    }.getType(), "金额", "amount")
            ),
            "filter1",
            Filter1.class
    ),
    Filter2("filter2",
            Lists.newArrayList(
                    new RuleParameter(TypeToken.getParameterized(List.class, String.class).getType(), "des1", "operation"),
                    new RuleParameter(new TypeToken<Double>() {
                    }.getType(), "金额", "amount")
            ),
            "filter2",
            Filter2.class
    );
    String des;
    List<RuleParameter> parameters;
    String functionName;
    Class<?> functionClass;


    FilterRuleFunction(String des, List<RuleParameter> parameters, String functionName, Class<?> functionClass) {
        // 校验参数是否重复
        Set<String> set = new HashSet<>();
        parameters.forEach(parameter -> {
            if (!set.add(parameter.getName())) {
                throw new RuntimeException("参数名重复：" + parameter.getName());
            }
        });

        // 校验是否存在有效的函数
        Method[] methods = functionClass.getMethods();
        long count = Arrays.stream(methods)
                .filter(method -> method.getName().equals(functionName))
                .count();
        if (count != 1) {
            throw new RuntimeException("不存在有效的函数：" + functionName);
        }

        // 校验函数的参数类型是否与 parameters 一致
        Method functionMethod = Arrays.stream(methods)
                .filter(method -> method.getName().equals(functionName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("找不到函数：" + functionName));

        Class<?>[] parameterTypes = functionMethod.getParameterTypes();
        Type[] genericParameterTypes = functionMethod.getGenericParameterTypes();

        if (parameterTypes.length != parameters.size()) {
            throw new RuntimeException("函数的参数数量与 parameters 不一致");
        }

        for (int i = 0; i < parameterTypes.length; i++) {
            System.out.println("parameterTypes[i]:" + parameterTypes[i]);
            System.out.println("genericParameterTypes[i]:" + genericParameterTypes[i]);
            System.out.println("parameters.get(i).getType():" + parameters.get(i).getType());
            if (!isParameterTypeValid(genericParameterTypes[i], parameters.get(i).getType())) {
                throw new RuntimeException("函数的参数类型与 parameters 不一致：" + parameterTypes[i]);
            }
        }

        this.des = des;
        this.parameters = parameters;
        this.functionName = functionName;
        this.functionClass = functionClass;
    }

    private static boolean isParameterTypeValid(Type parameterType, Type expectedType) {
        if (parameterType == null || expectedType == null) {
            throw new RuntimeException("参数类型不能为空");
        }
        if (parameterType.equals(expectedType)) {
            return true;
        } else if (parameterType instanceof ParameterizedType && expectedType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) parameterType;
            ParameterizedType expectedParameterizedType = (ParameterizedType) expectedType;

            if (parameterizedType.getRawType().equals(expectedParameterizedType.getRawType())) {
                Type[] typeArguments = parameterizedType.getActualTypeArguments();
                Type[] expectedTypeArguments = expectedParameterizedType.getActualTypeArguments();

                if (typeArguments.length == expectedTypeArguments.length) {
                    for (int i = 0; i < typeArguments.length; i++) {
                        if (!typeArguments[i].equals(expectedTypeArguments[i])) {
                            return false;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public Boolean executeFilter(RuleContext context, RuleEntity ruleEntity) {
        //规则中的具体参数值
        Map<String, Object> map = new Gson().fromJson(ruleEntity.getValue(), Map.class);
        Object[] objects = new Object[parameters.size()];
        for (int i = 0; i < parameters.size(); i++) {
            RuleParameter parameter = parameters.get(i);
            Object value = map.get(parameter.getName());
            log.info("parameter.getName()={},value={}", parameter.getName(), value);
            if (value == null) {
                throw new RuntimeException("参数不存在" + parameter.getName());
            }
            Type type = parameter.getType();
            if (!value.getClass().equals(type)) {
                throw new RuntimeException("参数类型不匹配" + parameter.getName());
            }
            objects[i] = value;
        }
        System.out.println("objects" + Arrays.toString(objects));
        //执行function
        try {
            // 获取指定的构造方法，这里示例获取带有一个int参数的构造方法
            Constructor<?> constructor = this.functionClass.getDeclaredConstructor(RuleContext.class);

            // 创建对象，传入构造方法所需的参数
            Object obj = this.functionClass.cast(constructor.newInstance(context));
            // 获取目标方法，这里示例调用Object类的toString方法
            Class[] classes = new Class[objects.length];
            //RuleParameter中的type是基本类型，需要转换为包装类型
            for (int i = 0; i < objects.length; i++) {
                classes[i] = objects[i].getClass();
            }
            Method method = obj.getClass().getMethod(functionName, classes);

            //使用参数调用方法
            Boolean result = (Boolean) method.invoke(obj, objects);
            System.out.println("result:" + result);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(FilterRuleFunction.Fileter1);

        //步骤1 定义上下文数据
        RuleContext ruleContext = new RuleContext();
        ruleContext.put(new RuleContextData(RuleContext.ContextKey.AMOYUNT, 3.0, Double.class));
        //步骤2 定义规则
        RuleEntity ruleEntity = new RuleEntity();
        ruleEntity.setKey("filter1");
        ruleEntity.setDes("filter1");
        Map<String, Object> map = Maps.newHashMap();
        map.put("operation", ">=");
        map.put("amount", 2);
        ruleEntity.setValue(new Gson().toJson(map));

        //步骤3 根据上下文和规则
        FilterRuleFunction.Fileter1.executeFilter(ruleContext, ruleEntity);
    }
}
