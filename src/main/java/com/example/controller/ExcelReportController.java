package com.example.controller;

import com.example.utils.ExcelExporterUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.report.ExcelReportService.*;

@RequestMapping("/excel")
public class ExcelReportController {
    private static final Class clazz = ExampleField.class;
    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    @PostMapping("/report")
    public void report(List<String> headers, List<String> data, List<String> modifierNames, String filename) {
        //步骤1 校验格式
        //校验表头和数据格式是否匹配
        if (headers.isEmpty() || data.isEmpty() || headers.size() != data.size()) {
            throw new RuntimeException("表头和数据格式不匹配");
        }

        //步骤2 转换数据格式
        List<Map<ExampleField, Object>> excelData = new ArrayList<>();
        try {
            for (String line : data) {
                Map<ExampleField, Object> map = gson.fromJson(line, Map.class);
                excelData.add(map);
            }
        } catch (Exception e) {
            throw new RuntimeException("数据格式错误");
        }

        List<Modifier> modifiers = modifierNames.stream().map(ExampleFieldModifier::valueOf).map(ExampleFieldModifier::getModifier).collect(Collectors.toList());

        //步骤3 导出excel
        ExcelExporterUtils exporter = new ExcelExporterUtils();
        exporter.exportToExcelWithModifier(headers, excelData, modifiers, "test.xlsx", clazz);
    }
}
