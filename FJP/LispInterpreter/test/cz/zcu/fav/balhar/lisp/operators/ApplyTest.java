package cz.zcu.fav.balhar.lisp.operators;

import cz.zcu.fav.balhar.lisp.exceptions.WrongAmountOfParameters;
import cz.zcu.fav.balhar.lisp.exceptions.WrongParameterTypesException;
import cz.zcu.fav.balhar.lisp.tree.Node;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 25.12.12
 * Time: 9:51
 */
public class ApplyTest {
    @Test
    public void testEvaluate() throws Exception {
        Apply apply = new Apply();
        apply.addParameter("#'-");
        apply.addParameter("[2, 1]");

        Node result = apply.evaluate();
        String expression = result.getExpression();
        assertEquals("1.0", expression);
    }

    @Test
    public void testEvaluateTwoFunctions() throws Exception {
        Apply apply = new Apply();
        apply.addParameter("#'-");
        apply.addParameter("#'list");
        apply.addParameter("[2, 1]");

        Node result = apply.evaluate();
        String expression = result.getExpression();
        assertEquals("1.0", expression);
    }

    @Test
    public void testEvaluateWrongArgs() throws Exception {
        Apply apply = new Apply();
        apply.addParameter("#'-");
        apply.addParameter("2");

        try{
            Node result = apply.evaluate();
            assertTrue(false);
        } catch(WrongParameterTypesException ex){
            assertTrue(true);
        }
    }

    @Test
    public void testEvaluateWrongArgs2() throws Exception {
        Apply apply = new Apply();
        apply.addParameter("[1, 2]");
        apply.addParameter("#'+");

        try{
            Node result = apply.evaluate();
            assertTrue(false);
        } catch(WrongParameterTypesException ex){
            assertTrue(true);
        }
    }

    @Test
    public void testEvaluateWrongArgs3() throws Exception {
        Apply apply = new Apply();
        apply.addParameter("[1, 2]");

        try{
            Node result = apply.evaluate();
            assertTrue(false);
        } catch(WrongAmountOfParameters ex){
            assertTrue(true);
        }
    }
}
