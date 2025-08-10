package com.iv127.kotlin.starter;

public class JavaPlatformTypeGetInteger {

    public Integer getInteger(int i) {
        if (i == 0) {
            return null;
        }

        return Integer.valueOf(i);
    }

}
