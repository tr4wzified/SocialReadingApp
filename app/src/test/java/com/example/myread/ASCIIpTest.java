package com.example.myread;

import org.junit.Test;

import static org.junit.Assert.*;

public class ASCIIpTest {
    @Test
    public void EmptyIsASCII() {
        assertTrue(GlobalFunctions.asciip(""));
    }

    @Test
    public void AlphaIsASCII() {
        assertTrue(GlobalFunctions.asciip("abcdefghijklmnopqrstuvwxyz"));
    }

    @Test
    public void NumberIsASCII() {
        assertTrue(GlobalFunctions.asciip("1234567890"));
    }

    @Test
    public void SpecialIsASCII() {
        assertTrue(GlobalFunctions.asciip("!@#$%^&*()_+=-"));
    }

    @Test
    public void DeadIsNotASCII() {
        assertFalse(GlobalFunctions.asciip("éáô"));
    }

    @Test
    public void EmojiIsNotASCII() {
        assertFalse(GlobalFunctions.asciip("👌"));
    }
}