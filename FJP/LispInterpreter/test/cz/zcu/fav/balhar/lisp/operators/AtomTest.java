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
 * Time: 9:54
 */
public class AtomTest {
    @Test
    public void testEvaluate() throws Exception {
        Atom atom = new Atom();
        atom.addParameter("[1, 2]");
        Node result = atom.evaluate();
        assertEquals("false", result.getExpression());
    }

    @Test
    public void testEvaluate2() throws Exception {
        Atom atom = new Atom();
        atom.addParameter("5.24");
        Node result = atom.evaluate();
        assertEquals("true", result.getExpression());
    }

    @Test
    public void testEvaluateTooManyParams() throws Exception {
        Atom atom = new Atom();
        atom.addParameter("5.24");
        atom.addParameter("[1, 2]");
        try {
            atom.evaluate();
            assertTrue(false);
        } catch(WrongAmountOfParameters ex){
            assertTrue(true);
        }
    }
}
