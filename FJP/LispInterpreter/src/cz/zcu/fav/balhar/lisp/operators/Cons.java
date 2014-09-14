package cz.zcu.fav.balhar.lisp.operators;

import cz.zcu.fav.balhar.lisp.LispList;
import cz.zcu.fav.balhar.lisp.exceptions.MalformedListException;
import cz.zcu.fav.balhar.lisp.exceptions.WrongAmountOfParameters;
import cz.zcu.fav.balhar.lisp.exceptions.WrongParameterTypesException;
import cz.zcu.fav.balhar.lisp.tree.Node;

/**
 * It takes two params and if the second is list add the first to the list. If the second
 * is not list create list with both parameters.
 *
 * @author Jakub Balhar
 */
public class Cons extends Operator {
    /**
     * {@inheritDoc}
     */
    @Override
    public Node evaluate() throws WrongAmountOfParameters, WrongParameterTypesException {
        if(parameters.size() != 2){
            throw new WrongAmountOfParameters();
        }

        try {
            String secondParam = parameters.get(1);
            LispList list;
            if(secondParam.equals("nil") || !secondParam.startsWith("[")){
                list = new LispList("[]");
                if(!secondParam.startsWith("[") && !secondParam.equals("nil")){
                    list.addToList(secondParam);
                }
            } else {
                list = new LispList(secondParam);
            }
            Node result = new Node(null);
            list.addToListBeginning(parameters.get(0));
            result.setExpression(list.toString());
            return result;
        } catch (MalformedListException e) {
            throw new WrongParameterTypesException();
        }
    }
}
