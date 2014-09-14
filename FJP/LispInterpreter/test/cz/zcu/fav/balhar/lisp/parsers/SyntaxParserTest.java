package cz.zcu.fav.balhar.lisp.parsers;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 18.12.12
 * Time: 10:59
 */
public class SyntaxParserTest {
    private SyntaxParser parser = new SyntaxParser();

    @Before
    public void test(){
    }

    @Test
    public void testEvaluatePlus() throws Exception {
        String toEvaluate = "(+ 2 5)";
        String expected = "7";
        String result = parser.evaluate(toEvaluate);
        assertEquals(expected, result);
    }

    @Test
    public void testEvaluatePlusMore() throws Exception {
        String toEvaluate = "(+ 2 5 8 4)";
        String expected = "19";
        String result = parser.evaluate(toEvaluate);
        assertEquals(expected, result);
    }

    @Test
    public void testEvaluateMultiply() throws Exception {
        String toEvaluate = "(* 2 5)";
        String expected = "10";
        String result = parser.evaluate(toEvaluate);
        assertEquals(expected, result);
    }

    @Test
    public void testEvaluateMultiplyMore() throws Exception {
        String toEvaluate = "(* 2 5 3 7)";
        String expected = "210";
        String result = parser.evaluate(toEvaluate);
        assertEquals(expected, result);
    }

    @Test
    public void testEvaluateMinus() throws Exception {
        String toEvaluate = "(- 2 5)";
        String expected = "-3";
        String result = parser.evaluate(toEvaluate);
        assertEquals(expected, result);
    }

    @Test
    public void testEvaluateDivide() throws Exception {
        String toEvaluate = "(/ 2 5)";
        String expected = "0.4";
        String result = parser.evaluate(toEvaluate);
        assertEquals(expected, result);
    }

    @Test
    public void testEvaluateEquals() throws Exception {
        String toEvaluate = "(= 2 5)";
        String expected = "false";
        String result = parser.evaluate(toEvaluate);
        assertEquals(expected, result);
    }

    @Test
    public void testEvaluateEqualsTrue() throws Exception {
        String toEvaluate = "(= 2 2)";
        String expected = "true";
        String result = parser.evaluate(toEvaluate);
        assertEquals(expected, result);
    }

    @Test
    public void testEvaluateSmaller() throws Exception {
        String toEvaluate = "(< 2 4)";
        String expected = "true";
        String result = parser.evaluate(toEvaluate);
        assertEquals(expected, result);
    }

    @Test
    public void testEvaluateBigger() throws Exception {
        String toEvaluate = "(> 2 4)";
        String expected = "false";
        String result = parser.evaluate(toEvaluate);
        assertEquals(expected, result);
    }

    @Test
    public void testEvaluateSmallerEqual() throws Exception {
        String toEvaluate = "(<= 2 4)";
        String expected = "true";
        String result = parser.evaluate(toEvaluate);
        assertEquals(expected, result);
    }

    @Test
    public void testEvaluateBiggerEqual() throws Exception {
        String toEvaluate = "(>= 2 4)";
        String expected = "false";
        String result = parser.evaluate(toEvaluate);
        assertEquals(expected, result);
    }

    @Test
    public void testEvaluateNonEqual() throws Exception {
        String toEvaluate = "(<> 2 4)";
        String expected = "true";
        String result = parser.evaluate(toEvaluate);
        assertEquals(expected, result);
    }
}
