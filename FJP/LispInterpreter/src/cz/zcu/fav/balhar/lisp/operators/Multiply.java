package cz.zcu.fav.balhar.lisp.operators;

import cz.zcu.fav.balhar.lisp.exceptions.WrongAmountOfParameters;
import cz.zcu.fav.balhar.lisp.exceptions.WrongParameterTypesException;
import cz.zcu.fav.balhar.lisp.tree.Node;

/**
 * It multiplies all parameters given as arguments.
 *
 * @author Jakub Balhar
 */
public class Multiply extends Operator {
    /**
     * {@inheritDoc}
     */
    @Override
    public Node evaluate() throws WrongAmountOfParameters, WrongParameterTypesException {
        if(parameters.size() < 1){
            throw new WrongAmountOfParameters();
        }

        try{
            double finalNumber = Double.parseDouble(parameters.remove(0));
            double numberToAdd;
            for(String param: parameters){
                numberToAdd = Double.parseDouble(param);
                finalNumber *= numberToAdd;
            }

            Node result = new Node(null);
            result.setExpression(String.valueOf(finalNumber));
            return result;
        } catch (NumberFormatException ex){
            throw new WrongParameterTypesException();
        }
    }
}
