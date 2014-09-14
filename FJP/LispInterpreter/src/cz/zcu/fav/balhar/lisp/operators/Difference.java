package cz.zcu.fav.balhar.lisp.operators;

import cz.zcu.fav.balhar.lisp.exceptions.WrongAmountOfParameters;
import cz.zcu.fav.balhar.lisp.tree.Node;

/**
 * It returns true if the parameters are different. It works even on nested lists.
 *
 * @version Jakub Balhar
 */
public class Difference extends Operator {
    /**
     * {@inheritDoc}
     */
    @Override
    public Node evaluate() throws WrongAmountOfParameters {
        if(parameters.size() != 2){
            throw new WrongAmountOfParameters();
        }

        boolean resultBool = !parameters.get(0).equals(parameters.get(1));
        Node result = new Node(null);
        result.setExpression(String.valueOf(resultBool));
        return result;
    }
}
