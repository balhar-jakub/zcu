package cz.zcu.fav.balhar.lisp.operators;

import cz.zcu.fav.balhar.lisp.tree.Node;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 25.12.12
 * Time: 10:53
 */
public class ListTest {
    @Test
    public void testEvaluate() throws Exception {
        List list = new List();
        list.addParameter("2");
        list.addParameter("[2, 5]");
        list.addParameter("4");
        Node result = list.evaluate();
        assertEquals("[2, [2, 5], 4]", result.getExpression());
    }

    @Test
    public void testEvaluate2() throws Exception {
        List list = new List();
        list.addParameter("2");
        Node result = list.evaluate();
        assertEquals("[2]", result.getExpression());
    }
}
