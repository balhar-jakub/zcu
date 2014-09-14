package cz.zcu.fav.balhar.lisp.operators;

import cz.zcu.fav.balhar.lisp.tree.Node;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 25.12.12
 * Time: 10:52
 */
public class LessEqualTest {
    @Test
    public void testEvaluate() throws Exception {
        LessEqual less = new LessEqual();
        less.addParameter("2");
        less.addParameter("5");
        Node result = less.evaluate();
        assertEquals("true", result.getExpression());
    }

    @Test
    public void testEvaluate2() throws Exception {
        LessEqual less = new LessEqual();
        less.addParameter("5");
        less.addParameter("2");
        Node result = less.evaluate();
        assertEquals("false", result.getExpression());
    }

    @Test
    public void testEvaluate3() throws Exception {
        LessEqual less = new LessEqual();
        less.addParameter("2");
        less.addParameter("2");
        Node result = less.evaluate();
        assertEquals("true", result.getExpression());
    }
}
