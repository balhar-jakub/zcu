package cz.zcu.fav.balhar.lisp.operators;

import cz.zcu.fav.balhar.lisp.tree.Node;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 25.12.12
 * Time: 10:36
 */
public class DivisionTest {
    @Test
    public void testEvaluate() throws Exception {
        Division division = new Division();
        division.addParameter("5");
        division.addParameter("2");
        Node result = division.evaluate();
        assertEquals("2.5", result.getExpression());
    }

    @Test
    public void testEvaluate2() throws Exception {
        Division division = new Division();
        division.addParameter("2");
        division.addParameter("2");
        Node result = division.evaluate();
        assertEquals("1.0", result.getExpression());
    }
}
