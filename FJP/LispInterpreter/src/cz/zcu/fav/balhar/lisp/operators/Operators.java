package cz.zcu.fav.balhar.lisp.operators;

import cz.zcu.fav.balhar.lisp.exceptions.NonExistingOperatorException;

/**
 * This class contains factory method to get correct operator based on its name.
 *
 * @author Jakub Balhar
 */
public class Operators {
    /**
     * Factory method returning correct operator based on its String representation.
     *
     * @param expression operator to find
     * @return Operator representing given name.
     * @throws NonExistingOperatorException
     */
    public static Operator getOperator(String expression) throws NonExistingOperatorException {
        if (expression.equals("car") || expression.equals("first")) {
            return new First();
        } else if (expression.equals("cdr") || expression.equals("rest")) {
            return new Rest();
        } else if (expression.equals("cons")) {
            return new Cons();
        } else if (expression.equals("atom")) {
            return new Atom();
        } else if (expression.equals("eq")) {
            return new Eq();
        } else if (expression.equals("apply")) {
            return new Apply();
        } else if (expression.equals("list")) {
            return new List();
        } else if (expression.equals("+")) {
            return new Plus();
        } else if (expression.equals("*")) {
            return new Multiply();
        } else if (expression.equals("-")) {
            return new Minus();
        } else if (expression.equals("/")) {
            return new Division();
        } else if (expression.equals("=")) {
            return new Equal();
        } else if (expression.equals("<")) {
            return new Less();
        } else if (expression.equals(">")) {
            return new More();
        } else if (expression.equals("<=")) {
            return new LessEqual();
        } else if (expression.equals(">=")) {
            return new MoreEqual();
        } else if (expression.equals("<>")) {
            return new Difference();
        }

        throw new NonExistingOperatorException();
    }
}
