package cz.zcu.fav.balhar.lisp.operators;

import cz.zcu.fav.balhar.lisp.tree.Node;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 25.12.12
 * Time: 10:44
 */
public class EqualTest {
    @Test
    public void testEvaluate() throws Exception {
        Eq eq = new Eq();
        eq.addParameter("[1, 2]");
        eq.addParameter("[1, 2]");
        Node result = eq.evaluate();
        assertEquals("true", result.getExpression());
    }

    @Test
    public void testEvaluate2() throws Exception {
        Eq eq = new Eq();
        eq.addParameter("[1]");
        eq.addParameter("[1, 2]");
        Node result = eq.evaluate();
        assertEquals("false", result.getExpression());
    }
}
