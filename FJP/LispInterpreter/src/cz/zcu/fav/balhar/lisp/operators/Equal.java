package cz.zcu.fav.balhar.lisp.operators;

import cz.zcu.fav.balhar.lisp.exceptions.WrongAmountOfParameters;
import cz.zcu.fav.balhar.lisp.exceptions.WrongParameterTypesException;
import cz.zcu.fav.balhar.lisp.tree.Node;

/**
 * It returns true if the two parameters are equal.
 *
 * @author Jakub Balhar
 */
public class Equal extends Operator {
    /**
     * {@inheritDoc}
     */
    @Override
    public Node evaluate() throws WrongAmountOfParameters, WrongParameterTypesException {
        if(parameters.size() != 2){
            throw new WrongAmountOfParameters();
        }

        try{
            double number1 = Double.parseDouble(parameters.get(0));
            double number2 = Double.parseDouble(parameters.get(1));
            boolean resultBool = (Math.abs(number1 - number2) < 0.0000001d);
            Node result = new Node(null);
            result.setExpression(String.valueOf(resultBool));
            return result;
        } catch(NumberFormatException ex){
            throw new WrongParameterTypesException();
        }
    }
}
