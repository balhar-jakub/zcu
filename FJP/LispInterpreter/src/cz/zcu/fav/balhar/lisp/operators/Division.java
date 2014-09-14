package cz.zcu.fav.balhar.lisp.operators;

import cz.zcu.fav.balhar.lisp.exceptions.WrongAmountOfParameters;
import cz.zcu.fav.balhar.lisp.exceptions.WrongParameterTypesException;
import cz.zcu.fav.balhar.lisp.tree.Node;

/**
 * It divides first parameter by the second one.
 *
 * @author Jakub Balhar
 */
public class Division extends Operator {
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

            double resultNumber = number1 / number2;
            Node result = new Node(null);
            result.setExpression(String.valueOf(resultNumber));
            return result;
        } catch (NumberFormatException ex){
            throw new WrongParameterTypesException();
        }
    }
}
