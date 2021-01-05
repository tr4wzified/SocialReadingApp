package com.example.myread;

public class GlobalFunctions {

    public static boolean asciip(String s)
    {
        return s.chars().allMatch(c -> c < 128);
    }
}
