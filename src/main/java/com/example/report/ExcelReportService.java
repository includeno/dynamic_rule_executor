package com.example.report;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelReportService {

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

//    public enum ExampleFieldModifier implements ExcelFieldModifierInterface {
//        AGE_MODIFIER(ExampleField.AGE, (value) -> {
//            if (value instanceof Integer) {
//                int age = (Integer) value;
//                return age + "岁";
//            }
//            throw new IllegalArgumentException("age must be Integer");
//        }, Integer.class);
//
//        private final ExcelFieldInterface field;
//        private final Modifier modifier;
//        private final Class<?> fieldType;
//
//        <T> ExampleFieldModifier(ExcelFieldInterface field, Function<T, String> modifier, Class<T> clazz) {
//            this.field = field;
//            this.fieldType = field.getFieldType();
//            if (!fieldType.equals(clazz)) {
//                throw new IllegalArgumentException("field type must be " + clazz.getName());
//            }
//            this.modifier = new Modifier(field, modifier);
//        }
//
//        @Override
//        public ExcelFieldInterface getField() {
//            return field;
//        }
//
//        @Override
//        public Modifier getModifier() {
//            return modifier;
//        }
//
//        public static Class<?> getFieldType(ExampleFieldModifier fieldModifier) {
//            return fieldModifier.fieldType;
//        }
//    }

    public static void main(String[] args) {
        exportToExcel(List.of("姓名", "年龄", "性别"), List.of(
                List.of("张三", 18, "男"),
                List.of("李四", 19, "女"),
                List.of("王五", 20, "男")
        ), "test.xlsx");
    }
}
