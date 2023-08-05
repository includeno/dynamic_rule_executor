package com.example.report;

import java.util.function.Function;

public enum ExampleFieldModifier implements ExcelFieldModifierInterface {
    AGE_MODIFIER(ExampleField.AGE, (value) -> {
        if (value instanceof Integer) {
            int age = (Integer) value;
            return age + "岁";
        }
        throw new IllegalArgumentException("age must be Integer");
    }, Integer.class);

    private final ExcelFieldInterface field;
    private final Modifier modifier;
    private final Class<?> fieldType;

    <T> ExampleFieldModifier(ExcelFieldInterface field, Function<T, String> modifier, Class<T> clazz) {
        this.field = field;
        this.fieldType = field.getFieldType();
        if (!fieldType.equals(clazz)) {
            throw new IllegalArgumentException("field type must be " + clazz.getName());
        }
        this.modifier = new Modifier(field, modifier);
    }

    @Override
    public ExcelFieldInterface getField() {
        return field;
    }

    @Override
    public Modifier getModifier() {
        return modifier;
    }

    public static Class<?> getFieldType(ExampleFieldModifier fieldModifier) {
        return fieldModifier.fieldType;
    }

}
