package com.example.test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class GsonExample {
    public static void main(String[] args) {
        String json = "[{\"name\":\"John\",\"age\":30},{\"name\":\"Alice\",\"age\":25}]";

        // 定义列表类型和元素类型的类对象
        Type listType = TypeToken.getParameterized(List.class, MyClass.class).getType();

        // 使用 Gson 解析 JSON 字符串
        Gson gson = new Gson();
        List<MyClass> myClassList = gson.fromJson(json, listType);

        // 打印解析结果
        for (MyClass obj : myClassList) {
            System.out.println(obj.getName() + ", " + obj.getAge());
        }
    }

    private static class MyClass {
        private String name;
        private int age;

        // Getters and setters
        // ...

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}
