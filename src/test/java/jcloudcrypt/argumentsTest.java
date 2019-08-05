package jcloudcrypt;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class argumentsTest {
    @Test
    public void checkForParseErrorsExisting() {
        String[] args = new String[] { "-f", "-h" };
        Arguments arguments = new Arguments(args);
        assertTrue(arguments.hasArgumentErrors());
    }

    @Test
    public void checkForParseErrorsNotExisting() {
        String[] args = new String[] { "-p 50", "-h" };
        Arguments arguments = new Arguments(args);
        assertFalse(arguments.hasArgumentErrors());
    }
}