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
public class RestTest {
    @Test
    public void testEvaluate() throws Exception {
        Rest first = new Rest();
        first.addParameter("[[1, 2], 3]");
        Node result = first.evaluate();
        assertEquals("3", result.getExpression());
    }
}
