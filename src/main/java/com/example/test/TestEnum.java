package com.example.test;

import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//验证枚举类中的参数类型是否与函数中的参数类型一致
public class TestEnum {
    enum EnumA {
        FUNCTION_A("exe",
                Lists.newArrayList(
                        new RuleParameter(TypeToken.getParameterized(List.class, String.class).getType(), "des1", "name1"),
                        new RuleParameter(new TypeToken<Double>() {
                        }.getType(), "金额", "amount")
                ),
                "exe",
                FunctionA.class
        );

        String des;
        List<RuleParameter> parameters;
        String functionName;
        Class<?> functionClass;

        EnumA(String des, List<RuleParameter> parameters, String functionName, Class<?> functionClass) {
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
    }

    public static class FunctionA {
        public Boolean exe(List<String> whiteList, Double amount) {
            System.out.println("exe");
            if (whiteList.contains("name1")) {
                return false;
            }
            System.out.println(amount);
            return true;
        }
    }

    public static void main(String[] args) {
        EnumA enumA = EnumA.FUNCTION_A;
        System.out.println("EnumA 构造方法参数类型验证通过");
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

    public static class RuleParameter {
        private Type type;
        private String description;
        private String name;

        public RuleParameter(Type type, String description, String name) {
            this.type = type;
            this.description = description;
            this.name = name;
        }

        public Type getType() {
            return type;
        }

        public String getDescription() {
            return description;
        }

        public String getName() {
            return name;
        }
    }
}
