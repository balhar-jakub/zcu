package cz.zcu.fav.balhar.lisp.operators;

import cz.zcu.fav.balhar.lisp.LispList;
import cz.zcu.fav.balhar.lisp.exceptions.MalformedListException;
import cz.zcu.fav.balhar.lisp.exceptions.WrongAmountOfParameters;
import cz.zcu.fav.balhar.lisp.exceptions.WrongParameterTypesException;
import cz.zcu.fav.balhar.lisp.tree.Node;

/**
 * It returns last element of the list.
 *
 * @author Jakub Balhar
 */
public class Rest extends Operator {
    /**
     * {@inheritDoc}
     */
    @Override
    public Node evaluate() throws WrongAmountOfParameters, WrongParameterTypesException {
        if(parameters.size() != 1){
            throw new WrongAmountOfParameters();
        }

        try {
            LispList list = new LispList(parameters.get(0));
            Node result = new Node(null);
            result.setExpression(list.removeLast());
            return result;
        } catch (MalformedListException e) {
            e.printStackTrace();
            throw new WrongParameterTypesException();
        }
    }
}
