package com.example.report;

import lombok.Data;

import java.util.List;

@Data
public class ExampleFieldWithModifier {
    private ExampleField field;
    private List<Modifier> modifiers;
}
