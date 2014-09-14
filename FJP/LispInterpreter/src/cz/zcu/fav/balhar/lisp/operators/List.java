package cz.zcu.fav.balhar.lisp.operators;

import cz.zcu.fav.balhar.lisp.LispList;
import cz.zcu.fav.balhar.lisp.exceptions.MalformedListException;
import cz.zcu.fav.balhar.lisp.exceptions.WrongAmountOfParameters;
import cz.zcu.fav.balhar.lisp.exceptions.WrongParameterTypesException;
import cz.zcu.fav.balhar.lisp.tree.Node;

/**
 * It creates List from all the parameters.
 *
 * @author Jakub Balhar
 */
public class List extends Operator {
    /**
     * {@inheritDoc}
     */
    @Override
    public Node evaluate() throws WrongAmountOfParameters, WrongParameterTypesException {
        if(parameters.size() < 1){
            throw new WrongAmountOfParameters();
        }

        try {
            LispList list = new LispList("[]");
            for(String param: parameters){
                list.addToList(param);
            }
            Node result = new Node(null);
            result.setExpression(list.toString());
            return result;
        } catch (MalformedListException e) {
            // This can never happen.
            throw new WrongParameterTypesException();
        }
    }
}
