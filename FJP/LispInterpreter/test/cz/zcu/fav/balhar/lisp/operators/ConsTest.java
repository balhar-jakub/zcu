package cz.zcu.fav.balhar.lisp.operators;

import cz.zcu.fav.balhar.lisp.exceptions.WrongAmountOfParameters;
import cz.zcu.fav.balhar.lisp.tree.Node;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 25.12.12
 * Time: 9:58
 */
public class ConsTest {
    @Test
    public void testEvaluate() throws Exception {
        Cons cons = new Cons();
        cons.addParameter("1");
        cons.addParameter("nil");
        Node result = cons.evaluate();
        assertEquals("[1]", result.getExpression());
    }

    @Test
    public void testEvaluate2() throws Exception {
        Cons cons = new Cons();
        cons.addParameter("1");
        cons.addParameter("[2]");
        Node result = cons.evaluate();
        assertEquals("[1, 2]", result.getExpression());
    }

    @Test
    public void testEvaluate3() throws Exception {
        Cons cons = new Cons();
        cons.addParameter("[1]");
        cons.addParameter("[2, 3]");
        Node result = cons.evaluate();
        assertEquals("[[1], 2, 3]", result.getExpression());
    }

    @Test
    public void testEvaluateNotEnoughParams() throws Exception {
        Cons cons = new Cons();
        cons.addParameter("[1]");
        cons.addParameter("[1]");
        cons.addParameter("[1]");
        try{
            cons.evaluate();
            assertTrue(false);
        } catch (WrongAmountOfParameters ex){
            assertTrue(true);
        }
    }

    @Test
    public void testEvaluateTooManyParams() throws Exception {
        Cons cons = new Cons();
        cons.addParameter("[1]");
        try{
            cons.evaluate();
            assertTrue(false);
        } catch (WrongAmountOfParameters ex){
            assertTrue(true);
        }
    }

    @Test
    public void testEvaluateSecondIsNotList() throws Exception {
        Cons cons = new Cons();
        cons.addParameter("[1]");
        cons.addParameter("5");
        Node result = cons.evaluate();
        assertEquals("[[1], 5]", result.getExpression());
    }
}
