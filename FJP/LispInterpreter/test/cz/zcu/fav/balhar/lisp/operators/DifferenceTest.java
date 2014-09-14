package cz.zcu.fav.balhar.lisp.operators;

import cz.zcu.fav.balhar.lisp.tree.Node;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 25.12.12
 * Time: 10:18
 */
public class DifferenceTest {
    @Test
    public void testEvaluate() throws Exception {
        Difference difference = new Difference();
        difference.addParameter("5");
        difference.addParameter("2");
        Node result = difference.evaluate();
        assertEquals("true",result.getExpression());
    }

    @Test
    public void testEvaluate2() throws Exception {
        Difference difference = new Difference();
        difference.addParameter("2");
        difference.addParameter("2");
        Node result = difference.evaluate();
        assertEquals("false",result.getExpression());
    }
}
