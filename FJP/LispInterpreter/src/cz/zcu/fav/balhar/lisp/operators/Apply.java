package cz.zcu.fav.balhar.lisp.operators;

import cz.zcu.fav.balhar.lisp.LispList;
import cz.zcu.fav.balhar.lisp.exceptions.MalformedListException;
import cz.zcu.fav.balhar.lisp.exceptions.NonExistingOperatorException;
import cz.zcu.fav.balhar.lisp.exceptions.WrongAmountOfParameters;
import cz.zcu.fav.balhar.lisp.exceptions.WrongParameterTypesException;
import cz.zcu.fav.balhar.lisp.tree.Node;

import java.util.ArrayList;
import java.util.Collection;

/**
 * It applies functions given as first parameters to last parameter which is list.
 *
 * @author Jakub Balhar
 */
public class Apply extends Operator {
    /**
     * {@inheritDoc}
     */
    @Override
    public Node evaluate() throws WrongAmountOfParameters, WrongParameterTypesException {
        if(parameters.size() < 2){
            throw new WrongAmountOfParameters();
        }
        if(!parameters.get(parameters.size() - 1).startsWith("[")){
            throw new WrongParameterTypesException();
        }

        ArrayList<String> functions = new ArrayList<String>();
        String listP = null;
        for(String params: parameters){
            if(params.startsWith("#'")){
                functions.add(params.substring(2));
            } else if(params.startsWith("[") && listP == null){
                listP = params;
            } else {
                throw new WrongParameterTypesException();
            }
        }

        if(listP == null || functions.size() < 1){
            throw new WrongParameterTypesException();
        }

        try {
            LispList list = new LispList(listP);
            Collection<String> atoms;
            Node resultT = null;
            String function;
            for(int actFncIdx = functions.size() - 1; actFncIdx >= 0; actFncIdx--){
                function = functions.get(actFncIdx);
                if(resultT != null){
                    list = new LispList(resultT.getExpression());
                }
                atoms = list.getAllAtoms();
                Operator operatorToUse = Operators.getOperator(function);
                for(String atom: atoms){
                    operatorToUse.addParameter(atom);
                }
                resultT = operatorToUse.evaluate();
            }

            return resultT;
        } catch (MalformedListException e) {
            throw new WrongParameterTypesException();
        } catch (NonExistingOperatorException e) {
            throw new WrongParameterTypesException();
        }
    }
}
