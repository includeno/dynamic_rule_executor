package com.example.rule;

import java.util.List;

public class RuleOperation {
    private static final List<String> numberOperations = List.of(">", "<", ">=", "<=", "==", "!=");

    private static List<String> stringOperations = List.of("==", "!=");

    public static List<String> getOperationsByType(Class<?> type) {
        if (type == String.class) {
            return stringOperations;
        }
        if (type == Double.class || type == Integer.class || type == Long.class) {
            return numberOperations;
        }
        throw new RuntimeException("不支持的类型" + type);
    }

    public static <T extends Comparable<T>> Boolean operation(String operation, T context, T amount) {
        if (context == null || amount == null) {
            throw new RuntimeException("参数不能为空");
        }
        int comparisonResult = context.compareTo(amount);
        switch (operation) {
            case ">":
                return comparisonResult > 0;
            case "<":
                return comparisonResult < 0;
            case ">=":
                return comparisonResult >= 0;
            case "<=":
                return comparisonResult <= 0;
            case "==":
                return comparisonResult == 0;
            case "!=":
                return comparisonResult != 0;
        }
        throw new RuntimeException("不支持的操作符" + operation);
    }

    public static void main(String[] args) {
        System.out.println(operation(">", 1.0, 2.0));
        System.out.println(operation("<", 2L, 2L));
        System.out.println(operation("!=", 2L, 3L));
    }
}
