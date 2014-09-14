package cz.zcu.fav.balhar.lisp.operators;

import cz.zcu.fav.balhar.lisp.tree.Node;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 25.12.12
 * Time: 10:57
 */
public class MinusTest {
    @Test
    public void testEvaluate() throws Exception {
        Minus minus = new Minus();
        minus.addParameter("4");
        minus.addParameter("2");
        Node result = minus.evaluate();
        assertEquals("2.0", result.getExpression());
    }
}
