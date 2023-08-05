package com.example.utils;

import com.example.report.*;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.CollectionUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.example.report.ExcelReportService.*;

@Slf4j
public class ExcelExporterUtils {

    public static void main(String[] args) {
        List<ExampleField> headerFields = List.of(ExampleField.NAME, ExampleField.AGE, ExampleField.GENDER);
        List<String> headers = headerFields.stream().map(ExampleField::getFieldName).collect(Collectors.toList());
        List<Map<ExampleField, Object>> data = Lists.newArrayList(
                Map.of(ExampleField.NAME, "张三", ExampleField.AGE, 18, ExampleField.GENDER, "男"),
                Map.of(ExampleField.NAME, "李四", ExampleField.AGE, 19, ExampleField.GENDER, "女"),
                Map.of(ExampleField.NAME, "王五", ExampleField.AGE, 20, ExampleField.GENDER, "男")
        );

        //添加modify配置
        List<Modifier> modifiers = Lists.newArrayList(
                ExampleFieldModifier.AGE_MODIFIER.getModifier()
        );

        ExcelExporterUtils exporter = new ExcelExporterUtils();
        //exporter.exportToExcel(headers, data, "test.xlsx", ExampleField.class);
        exporter.exportToExcelWithModifier(headers, data, modifiers, "testWithModifier.xlsx", ExampleField.class);
    }

    public static boolean check(List<String> headers, Class<?> clazz) {
        preCheckClass(clazz);
        checkExcelTitle(headers, clazz);
        return true;
    }

    //检查Excel Title
    private static boolean checkExcelTitle(List<String> headers, Class<?> clazz) {
        //检查表头是否为空
        if (CollectionUtils.isEmpty(headers)) {
            throw new IllegalArgumentException("headers cannot be empty");
        }
        //检查表头是否与枚举类中的字段一一对应

        return true;
    }

    private static boolean preCheckClass(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz cannot be null");
        }
        if (clazz.getEnumConstants() == null) {
            throw new IllegalArgumentException("clazz must be enum");
        }
        // 检查clazz是否实现了ExcelFieldInterface接口
        if (!ExcelFieldInterface.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("clazz must implement ExcelFieldInterface");
        }
        return true;
    }

    public <T> void exportToExcel(List<String> headers, List<Map<T, Object>> data, String filename, Class<T> clazz) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");

            // 创建表头行
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                Cell headerCell = headerRow.createCell(i);
                headerCell.setCellValue(headers.get(i));
            }

            // 填充数据
            for (int i = 0; i < data.size(); i++) {
                Row dataRow = sheet.createRow(i + 1);
                Map<T, Object> rowData = data.get(i);
                for (int j = 0; j < headers.size(); j++) {
                    Cell dataCell = dataRow.createCell(j);
                    String header = headers.get(j);
                    for (T field : rowData.keySet()) {
                        ExcelFieldInterface excelField = (ExcelFieldInterface) field;
                        if (excelField.getFieldName().equals(header)) {
                            Object value = rowData.get(excelField);
                            value = applyModifier(excelField, value); // 应用修改器调整字段格式
                            setCellValue(dataCell, value);
                            break;
                        }
                    }
                }
            }

            // 保存为.xlsx文件
            try (FileOutputStream fileOut = new FileOutputStream(filename)) {
                workbook.write(fileOut);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <T> void exportToExcelWithModifier(List<String> headers, List<Map<T, Object>> data, List<Modifier> modifiers, String filename, Class<T> clazz) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");

            // 创建表头行
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                Cell headerCell = headerRow.createCell(i);
                headerCell.setCellValue(headers.get(i));
            }

            // 填充数据
            for (int i = 0; i < data.size(); i++) {
                Row dataRow = sheet.createRow(i + 1);
                Map<T, Object> rowData = data.get(i);
                List<T> unusedFields = new ArrayList<>();
                for (int j = 0; j < headers.size(); j++) {
                    Cell dataCell = dataRow.createCell(j);
                    String header = headers.get(j);
                    for (T field : rowData.keySet()) {
                        ExcelFieldInterface excelField = (ExcelFieldInterface) field;
                        if (excelField.getFieldName().equals(header)) {
                            log.info("field found: {} header:{}", excelField, header);
                            Object value = rowData.get(excelField);
                            value = applyModifier(excelField, value, modifiers); // 应用修改器调整字段格式
                            setCellValue(dataCell, value);
                            unusedFields.add(field);
                            break;
                        } else {
                            log.warn("field not found: {} header:{}", excelField, header);
                        }
                    }
                }
                if (unusedFields.size() != rowData.keySet().size()) {
                    //打印未使用的字段
                    List<T> unused = rowData.keySet().stream().filter(field -> !unusedFields.contains(field)).collect(Collectors.toList());
                    log.error("field not found: {}", unused);
                }
            }

            // 保存为.xlsx文件
            try (FileOutputStream fileOut = new FileOutputStream(filename)) {
                workbook.write(fileOut);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void setCellValue(Cell cell, Object value) {
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } // 根据需要添加其他类型的处理
    }

    private static Object applyModifier(ExcelFieldInterface field, Object value) {
        // 这里可以根据字段类型和需求对字段值进行修改
        if (field.getFieldName() == ExampleField.AGE.getFieldName() && value instanceof Integer) {
            int age = (Integer) value;
            return age + "岁";
        }
        return value;
    }

    private static String applyModifier(ExcelFieldInterface field, Object value, List<Modifier> modifiers) {
        // 这里可以根据字段类型和需求对字段值进行修改
        List<Modifier> modifierList = modifiers.stream().filter(modifier -> modifier.getField().equals(field)).collect(Collectors.toList());
        if (modifierList.size() > 0) {
            return (String) modifierList.get(0).getModifier().apply(value);
        } else {
            log.warn("modifier not found for field: {}", field);
            return String.valueOf(value);
        }
    }
}
