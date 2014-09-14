package cz.zcu.fav.balhar.lisp.operators;

import cz.zcu.fav.balhar.lisp.exceptions.WrongAmountOfParameters;
import cz.zcu.fav.balhar.lisp.tree.Node;

/**
 * It returns true if the two parameters are equal.
 *
 * @author Jakub Balhar
 */
public class Eq extends Operator {
    /**
     * {@inheritDoc}
     */
    @Override
    public Node evaluate() throws WrongAmountOfParameters {
        if(parameters.size() != 2){
            throw new WrongAmountOfParameters();
        }

        boolean resultBool = parameters.get(0).equals(parameters.get(1));
        Node result = new Node(null);
        result.setExpression(String.valueOf(resultBool));
        return result;
    }
}
