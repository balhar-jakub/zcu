package cz.zcu.fav.balhar.lisp.operators;

import cz.zcu.fav.balhar.lisp.tree.Node;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 25.12.12
 * Time: 11:01
 */
public class MultiplyTest {
    @Test
    public void testEvaluate() throws Exception {
        Multiply minus = new Multiply();
        minus.addParameter("4");
        minus.addParameter("2");
        Node result = minus.evaluate();
        assertEquals("8.0", result.getExpression());
    }

    @Test
    public void testEvaluate2() throws Exception {
        Multiply minus = new Multiply();
        minus.addParameter("4");
        minus.addParameter("2");
        minus.addParameter("3");
        minus.addParameter("5");
        Node result = minus.evaluate();
        assertEquals("120.0", result.getExpression());
    }
}
