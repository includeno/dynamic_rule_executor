package com.example.report;

import lombok.Data;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

public class ExcelReportService {
    public interface ExcelFieldInterface {
        String getFieldName();

        Class<?> getFieldType();
    }

    public interface ExcelFieldModifierInterface {
        ExcelFieldInterface getField();

        Modifier getModifier();

    }

    @Data
    public static class Modifier<T> {
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

    public enum ExampleField implements ExcelFieldInterface {
        ID("id", Integer.class),
        NAME("name", String.class),
        AGE("age", Integer.class),
        SALARY("salary", Double.class);

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

    //导出Excel
    public static void exportToExcel(List<String> headers, List<List<Object>> data, String filename) {
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
                List<Object> rowData = data.get(i);
                for (int j = 0; j < rowData.size(); j++) {
                    Cell dataCell = dataRow.createCell(j);
                    Object value = rowData.get(j);
                    if (value instanceof String) {
                        dataCell.setCellValue((String) value);
                    } else if (value instanceof Integer) {
                        dataCell.setCellValue((Integer) value);
                    } else if (value instanceof Double) {
                        dataCell.setCellValue((Double) value);
                    } // 根据需要添加其他类型的处理
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

    public static void main(String[] args) {
        exportToExcel(List.of("姓名", "年龄", "性别"), List.of(
                List.of("张三", 18, "男"),
                List.of("李四", 19, "女"),
                List.of("王五", 20, "男")
        ), "test.xlsx");
    }
}
