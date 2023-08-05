package com.example.report;

public enum ExampleField implements ExcelFieldInterface {
    ID("id", Integer.class),
    NAME("姓名", String.class),
    AGE("年龄", Integer.class),
    GENDER("性别", Double.class);

    private final String fieldName;
    private final Class<?> fieldType;

    ExampleField(String fieldName, Class<?> fieldType) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public Class<?> getFieldType() {
        return fieldType;
    }
}