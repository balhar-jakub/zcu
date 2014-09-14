package cz.zcu.fav.balhar.lisp.operators;

import cz.zcu.fav.balhar.lisp.exceptions.WrongAmountOfParameters;
import cz.zcu.fav.balhar.lisp.exceptions.WrongParameterTypesException;
import cz.zcu.fav.balhar.lisp.tree.Node;

/**
 * Both parameters must be number. It returns if the first parameter is more then second
 * number.
 *
 * @author Jakub Balhar
 */
public class More extends Operator {
    /**
     * {@inheritDoc}
     */
    @Override
    public Node evaluate() throws WrongParameterTypesException, WrongAmountOfParameters {
        if(parameters.size() != 2){
            throw new WrongAmountOfParameters();
        }

        try{
            double number1 = Double.parseDouble(parameters.get(0));
            double number2 = Double.parseDouble(parameters.get(1));

            boolean resultNumber = number1 > number2;
            Node result = new Node(null);
            result.setExpression(String.valueOf(resultNumber));
            return result;
        } catch (NumberFormatException ex){
            throw new WrongParameterTypesException();
        }
    }
}
