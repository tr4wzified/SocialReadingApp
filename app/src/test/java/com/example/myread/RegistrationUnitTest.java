package com.example.myread;

import org.junit.Test;

import static com.example.myread.RegisterActivity.passwordComplexityTest;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class RegistrationUnitTest {
    @Test
    public void validatePasswordTest() {
        // Password is minimally 9 chars long.
        assertFalse(passwordComplexityTest("Pa0!aaaa"));
        // Password needs special char
        assertFalse(passwordComplexityTest("Passw0rdwithoutspecial"));
        // Password needs to be 64 chars or less
        assertFalse(passwordComplexityTest("Passw0rd!aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")); //65 chars
        assertTrue(passwordComplexityTest("Passw0rd!aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")); //64 chars
        // Password needs capital
        assertFalse(passwordComplexityTest("passw0rd!"));
        // Password needs number
        assertFalse(passwordComplexityTest("Password!"));

        // Known correct pass
        assertTrue(passwordComplexityTest("Passw0rd!"));
    }
}