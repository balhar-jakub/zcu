package cz.zcu.fav.balhar.lisp.operators;

import cz.zcu.fav.balhar.lisp.exceptions.WrongAmountOfParameters;
import cz.zcu.fav.balhar.lisp.exceptions.WrongParameterTypesException;
import cz.zcu.fav.balhar.lisp.tree.Node;

/**
 * It sums all parameters. There must be at least one parameter.
 *
 * @author Jakub Balhar
 */
public class Plus extends Operator {
    /**
     * {@inheritDoc}
     */
    @Override
    public Node evaluate() throws WrongAmountOfParameters, WrongParameterTypesException {
        if(parameters.size() < 1){
            throw new WrongAmountOfParameters();
        }

        try{
            double finalNumber = 0d, numberToAdd;
            for(String param: parameters){
                numberToAdd = Double.parseDouble(param);
                finalNumber += numberToAdd;
            }

            Node result = new Node(null);
            result.setExpression(String.valueOf(finalNumber));
            return result;
        } catch (NumberFormatException ex){
            throw new WrongParameterTypesException();
        }
    }
}
