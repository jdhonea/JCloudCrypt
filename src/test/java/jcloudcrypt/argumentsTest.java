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
        String[] args = new String[] { "-p", "50", "-h" };
        Arguments arguments = new Arguments(args);
        assertFalse(arguments.hasArgumentErrors());
    }

    @Test
    public void checkForConflictsExisting() {
        String[] args = new String[] { "-e", "test", "-d", "test" };
        Arguments arguments = new Arguments(args);
        assertTrue(arguments.checkForConflicts());
    }

    @Test
    public void checkForConflictsNoArgs() {
        String[] args = new String[0];
        Arguments arguments = new Arguments(args);
        assertTrue(arguments.checkForConflicts());
    }

    @Test
    public void checkForConflictsNotExisting() {
        String[] args = new String[] { "-e", "test" };
        Arguments arguments = new Arguments(args);
        assertFalse(arguments.checkForConflicts());
    }

    @Test
    public void checkMemAboveBounds() {
        String[] args = new String[] { "-m", "1025" };
        Arguments arguments = new Arguments(args);
        assertTrue(arguments.checkOutOfMemBounds());
    }

    @Test
    public void checkMemBelowBounds() {
        String[] args = new String[] { "-m", "0" };
        Arguments arguments = new Arguments(args);
        assertTrue(arguments.checkOutOfMemBounds());
    }

    @Test
    public void checkMemInBounds() {
        String[] args = new String[] { "-m", "10" };
        Arguments arguments = new Arguments(args);
        assertFalse(arguments.checkOutOfMemBounds());
    }

    @Test
    public void checkMemNonNumeric() {
        String[] args = new String[] { "-m", "test" };
        Arguments arguments = new Arguments(args);
        assertTrue(arguments.checkOutOfMemBounds());
    }

    @Test
    public void checkParAboveBounds() {
        String[] args = new String[] { "-p", "33" };
        Arguments arguments = new Arguments(args);
        assertTrue(arguments.checkOutOfParallelismBounds());
    }
}