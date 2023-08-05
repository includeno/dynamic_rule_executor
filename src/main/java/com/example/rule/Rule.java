package com.example.rule;

public interface Rule<T> {
    boolean isMatch(T data);
}
