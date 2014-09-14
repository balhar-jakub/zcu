package cz.zcu.fav.balhar.lisp.parsers;

import cz.zcu.fav.balhar.lisp.exceptions.NonExistingOperatorException;
import cz.zcu.fav.balhar.lisp.exceptions.WrongAmountOfParameters;
import cz.zcu.fav.balhar.lisp.exceptions.WrongParameterTypesException;
import cz.zcu.fav.balhar.lisp.operators.Operator;
import cz.zcu.fav.balhar.lisp.operators.Operators;
import cz.zcu.fav.balhar.lisp.tree.Node;

import java.util.List;

/**
 * It evaluates tree of expressions and its parameters in final result.
 *
 * @author Jakub Balhar
 */
public class Evaluator {
    private Node tree;

    public Evaluator(Node tree) {
        this.tree = tree;
    }

    /**
     * It evaluates tree which it got as parameter.
     *
     * @return Evaluated lisp string.
     * @throws WrongAmountOfParameters
     * @throws NonExistingOperatorException
     * @throws WrongParameterTypesException
     */
    public String evaluate() throws WrongAmountOfParameters, NonExistingOperatorException, WrongParameterTypesException {
        Node result = evaluateNode(tree);
        return result.getExpression();
    }

    /**
     * It evaluates one node. This function is used recursively.
     *
     * @param toEvaluate Node to evaluate.
     * @return Nde containing the result.
     * @throws NonExistingOperatorException
     * @throws WrongAmountOfParameters
     * @throws WrongParameterTypesException
     */
    public Node evaluateNode(Node toEvaluate) throws NonExistingOperatorException, WrongAmountOfParameters, WrongParameterTypesException {
        List<Node> otherDescendants = toEvaluate.getOtherDescendants();
        List<Node> leafDescendants = toEvaluate.getLeafDescendants();
        for(Node complexDescendant: otherDescendants){
            leafDescendants.add(evaluateNode(complexDescendant));
        }

        Operator operator = Operators.getOperator(toEvaluate.getExpression()); // Based on Expression get correct Operator
        for(Node leaf: leafDescendants){
            operator.addParameter(leaf.getExpression());
        }
        Node result = operator.evaluate();
        return result;
    }
}
