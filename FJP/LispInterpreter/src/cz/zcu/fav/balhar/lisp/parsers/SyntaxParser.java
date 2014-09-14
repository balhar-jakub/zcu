package cz.zcu.fav.balhar.lisp.parsers;

import cz.zcu.fav.balhar.lisp.exceptions.NonExistingOperatorException;
import cz.zcu.fav.balhar.lisp.exceptions.WrongAmountOfParameters;
import cz.zcu.fav.balhar.lisp.exceptions.WrongParameterTypesException;
import cz.zcu.fav.balhar.lisp.tree.Node;

import java.util.List;

/**
 * It gets atomic expressions from lexical parser and creates tree from them, which is then given to Evaluator
 * to evaluate.
 *
 * @author Jakub Balhar
 */
public class SyntaxParser {
    /**
     * It evaluates the expression which is passed to it.
     *
     * @param expression expression to evaluate.
     * @return Result of the expression.
     * @throws WrongAmountOfParameters
     * @throws NonExistingOperatorException
     * @throws WrongParameterTypesException
     */
    public String evaluate(String expression) throws WrongAmountOfParameters, NonExistingOperatorException, WrongParameterTypesException {
        LexicalParser parser = new LexicalParser(expression);
        Node tree = createTree(parser.getAllAtomics());
        Evaluator evaluator = new Evaluator(tree);
        return evaluator.evaluate();
    }

    /**
     * It creates tree from the atomic expressions.
     *
     * @param allAtomic All atomic expressions. It can be a list.
     * @return Root of the tree.
     */
    public Node createTree(List<String> allAtomic) {
        Node actualNode = null, descendant = null;
        // Every node has expression and then descendant nodes representing parameters.
        for(String atomic: allAtomic){
            if(atomic.equals("(")){
                actualNode = new Node(actualNode);
            } else if(atomic.equals(")")){
                if(actualNode.getParent() != null){
                    actualNode.getParent().addDescendant(actualNode);
                    actualNode = actualNode.getParent();
                }
            } else {
                if(!actualNode.hasExpression()){
                    actualNode.setExpression(atomic);
                } else {
                    descendant = new Node(actualNode);
                    descendant.setAsLeaf();
                    descendant.setExpression(atomic);
                    actualNode.addDescendant(descendant);
                }
            }
        }

        return actualNode;
    }
}
