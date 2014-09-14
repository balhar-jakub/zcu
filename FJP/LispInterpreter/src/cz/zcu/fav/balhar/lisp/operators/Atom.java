package cz.zcu.fav.balhar.lisp.operators;

import cz.zcu.fav.balhar.lisp.exceptions.WrongAmountOfParameters;
import cz.zcu.fav.balhar.lisp.exceptions.WrongParameterTypesException;
import cz.zcu.fav.balhar.lisp.tree.Node;

/**
 * It returns if the parameter is atomic expression o list.
 *
 * @author Jakub Balhar
 */
public class Atom extends Operator {
    /**
     * {@inheritDoc}
     */
    @Override
    public Node evaluate() throws WrongAmountOfParameters, WrongParameterTypesException {
        if (parameters.size() != 1) {
            throw new WrongAmountOfParameters();
        }

        boolean resultNumber = !parameters.get(0).startsWith("[");
        Node result = new Node(null);
        result.setExpression(String.valueOf(resultNumber));
        return result;
    }
}
