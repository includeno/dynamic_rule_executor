package com.example.utils;

import com.example.rule.RuleParameter;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TypeUtils {
    public static Boolean typeEquals(Type type, Object value) {
        Preconditions.checkNotNull(type, "type is null");
        Preconditions.checkNotNull(value, "value is null");
        Type rawType = null;
        Class rawClass = null;
        List<Type> genericTypeList = new ArrayList<>();
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            // 获取泛型参数的Type对象
            rawType = parameterizedType.getRawType();
            for (Type t : actualTypeArguments) {
                genericTypeList.add(t);
            }
        }
        System.out.println("======");
        System.out.println("type:" + type);
        System.out.println("type.getTypeName():" + type.getTypeName());
        System.out.println("value.getClass():" + value.getClass());
        System.out.println("rawType:" + rawType);
        System.out.println("genericTypeList:" + genericTypeList);
        System.out.println("value.getClass():" + value.getClass());
        System.out.println("======");

        try {
            System.out.println("loading rawType:" + rawType);
            Class<?> clazz = Class.forName(rawType.getTypeName());
            // 使用获取到的Class对象进行后续操作
            rawClass = clazz;
        } catch (Exception e) {
            // 处理类找不到的异常
            rawClass = null;
        }

        if (value.getClass().equals(type)) {
            //非参数类型 匹配成功
            System.out.println("非参数类型 匹配成功");
            //throw new RuntimeException("参数类型不匹配" + parameter.getName());
        }
        //判断参数化类型情况
        //1.类型完全相同
        //2.类型不同，但是是父子关系
        else if (rawType != null && !genericTypeList.isEmpty() && (value.getClass().getName() == type.getTypeName() || value.getClass().isAssignableFrom(rawClass) || rawClass.isAssignableFrom(value.getClass()))) {
            //参数类型 匹配成功
            //throw new RuntimeException("参数类型不匹配" + parameter.getName());
            System.out.println("参数类型 匹配成功");
            System.out.println("rawType:" + rawType);
            System.out.println("genericTypeList:" + genericTypeList);
            System.out.println("value.getClass():" + value.getClass());
            System.out.println("type.getTypeName():" + type.getTypeName());
        } else {
            throw new RuntimeException("参数类型不匹配");
        }
        return Boolean.TRUE;
    }

    public static Boolean typeEquals(RuleParameter ruleParameter, Object value) {
        Preconditions.checkNotNull(ruleParameter, "ruleParameter is null");
        Preconditions.checkNotNull(value, "value is null");
        return typeEquals(ruleParameter.getType(), value);
    }
}
