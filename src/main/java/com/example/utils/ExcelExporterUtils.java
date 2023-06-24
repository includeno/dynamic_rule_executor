package com.example.utils;

import com.example.report.ExcelReportService;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class ExcelExporterUtils<T extends ExcelReportService.ExcelFieldInterface> {

    public static void main(String[] args) {
        List<String> headers = List.of("姓名", "年龄", "性别");
        List<Map<ExampleField, Object>> data = Lists.newArrayList(
                Map.of(ExampleField.NAME, "张三", ExampleField.AGE, 18, ExampleField.GENDER, "男"),
                Map.of(ExampleField.NAME, "李四", ExampleField.AGE, 19, ExampleField.GENDER, "女"),
                Map.of(ExampleField.NAME, "王五", ExampleField.AGE, 20, ExampleField.GENDER, "男")
        );

        //添加modify配置
        List<ExcelReportService.Modifier> modifiers = Lists.newArrayList(
                ExampleFieldModifier.AGE_MODIFIER.getModifier()
        );

        ExcelExporterUtils exporter = new ExcelExporterUtils();
        //exporter.exportToExcel(headers, data, "test.xlsx", ExampleField.class);
        exporter.exportToExcelWithModifier(headers, data, modifiers, "testWithModifier.xlsx", ExampleField.class);
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
                        ExcelReportService.ExcelFieldInterface excelField = (ExcelReportService.ExcelFieldInterface) field;
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

    public <T> void exportToExcelWithModifier(List<String> headers, List<Map<T, Object>> data, List<ExcelReportService.Modifier> modifiers, String filename, Class<T> clazz) {
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
                        ExcelReportService.ExcelFieldInterface excelField = (ExcelReportService.ExcelFieldInterface) field;
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

    private static Object applyModifier(ExcelReportService.ExcelFieldInterface field, Object value) {
        // 这里可以根据字段类型和需求对字段值进行修改
        if (field.getFieldName() == ExampleField.AGE.getFieldName() && value instanceof Integer) {
            int age = (Integer) value;
            return age + "岁";
        }
        return value;
    }

    private static String applyModifier(ExcelReportService.ExcelFieldInterface field, Object value, List<ExcelReportService.Modifier> modifiers) {
        // 这里可以根据字段类型和需求对字段值进行修改
        List<ExcelReportService.Modifier> modifierList = modifiers.stream().filter(modifier -> modifier.getField().equals(field)).collect(Collectors.toList());
        if (modifierList.size() > 0) {
            return (String) modifierList.get(0).getModifier().apply(value);
        }
        else{
            log.warn("modifier not found for field: {}", field);
            return String.valueOf(value);
        }
    }

    public interface ExcelFieldInterface {
        String getFieldName();

        Class<?> getFieldType();
    }

    public enum ExampleField implements ExcelReportService.ExcelFieldInterface {
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

    public enum ExampleFieldModifier implements ExcelReportService.ExcelFieldModifierInterface {
        AGE_MODIFIER(ExampleField.AGE, (value) -> {
            if (value instanceof Integer) {
                int age = (Integer) value;
                return age + "岁";
            }
            throw new IllegalArgumentException("age must be Integer");
        }, Integer.class);

        private final ExcelReportService.ExcelFieldInterface field;
        private final ExcelReportService.Modifier modifier;
        private final Class<?> fieldType;

        <T> ExampleFieldModifier(ExcelReportService.ExcelFieldInterface field, Function<T, String> modifier, Class<T> clazz) {
            this.field = field;
            this.fieldType = field.getFieldType();
            if (!fieldType.equals(clazz)) {
                throw new IllegalArgumentException("field type must be " + clazz.getName());
            }
            this.modifier = new ExcelReportService.Modifier(field, modifier);
        }

        @Override
        public ExcelReportService.ExcelFieldInterface getField() {
            return field;
        }

        @Override
        public ExcelReportService.Modifier getModifier() {
            return modifier;
        }

        public static Class<?> getFieldType(ExampleFieldModifier fieldModifier) {
            return fieldModifier.fieldType;
        }
    }
}
