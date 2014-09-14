package cz.zcu.fav.balhar.lisp.operators;

import cz.zcu.fav.balhar.lisp.tree.Node;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 25.12.12
 * Time: 10:45
 */
public class FirstTest {
    @Test
    public void testEvaluate() throws Exception {
        First first = new First();
        first.addParameter("[[1, 2], 3]");
        Node result = first.evaluate();
        assertEquals("[1, 2]", result.getExpression());
    }
}
