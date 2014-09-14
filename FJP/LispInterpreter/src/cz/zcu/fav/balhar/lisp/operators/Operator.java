package cz.zcu.fav.balhar.lisp.operators;

import cz.zcu.fav.balhar.lisp.exceptions.WrongAmountOfParameters;
import cz.zcu.fav.balhar.lisp.exceptions.WrongParameterTypesException;
import cz.zcu.fav.balhar.lisp.tree.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * This is base class for every Operator in this application. Operators are created in factory
 * method in class Operators.
 *
 * @author Jakub Balhar
 */
abstract public class Operator {
    protected List<String> parameters = new ArrayList<String>();

    /**
     * It adds another parameter to this operator. Operator works upon added parameters.
     *
     * @param expression parameter to add. It must be either list or atomic expression
     */
    public void addParameter(String expression) {
        parameters.add(expression);
    }

    /**
     * Every Operator must implement this method. It is called when all parameters are set to get
     * result of this Operator as a new Node.
     *
     * @return It returns Node containing
     * @throws WrongAmountOfParameters
     * @throws WrongParameterTypesException
     */
    abstract public Node evaluate() throws WrongAmountOfParameters, WrongParameterTypesException;
}
