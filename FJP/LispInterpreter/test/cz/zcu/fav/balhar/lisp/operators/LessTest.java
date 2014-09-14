package cz.zcu.fav.balhar.lisp.operators;

import cz.zcu.fav.balhar.lisp.tree.Node;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 25.12.12
 * Time: 10:48
 */
public class LessTest {
    @Test
    public void testEvaluate() throws Exception {
        Less less = new Less();
        less.addParameter("2");
        less.addParameter("5");
        Node result = less.evaluate();
        assertEquals("true", result.getExpression());
    }

    @Test
    public void testEvaluate2() throws Exception {
        Less less = new Less();
        less.addParameter("5");
        less.addParameter("2");
        Node result = less.evaluate();
        assertEquals("false", result.getExpression());
    }

    @Test
    public void testEvaluate3() throws Exception {
        Less less = new Less();
        less.addParameter("2");
        less.addParameter("2");
        Node result = less.evaluate();
        assertEquals("false", result.getExpression());
    }
}

// TODO lexical parser ignore more spaces.
