package com.example.report;

import lombok.Data;

import java.util.function.Function;

@Data
public class Modifier<T> {
    private ExcelFieldInterface field;
    private Function<T, String> modifier;//修改器

    public Modifier(ExcelFieldInterface field, Function<T, String> modifier) {
        this.field = field;
        this.modifier = modifier;
    }

    //进行数据转化
    public String apply(T value) {
        return modifier.apply(value);
    }
}