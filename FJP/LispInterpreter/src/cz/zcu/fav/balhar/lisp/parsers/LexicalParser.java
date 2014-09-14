package cz.zcu.fav.balhar.lisp.parsers;

import java.util.ArrayList;
import java.util.List;

/**
 * It parses given string from lexical point of view to atomic expressions and lists and operators.
 *
 * @author Jakub Balhar
 */
public class LexicalParser {
    private int actPos;
    private List<String> atomicExpression;
    private String atomic;

    /**
     * String to parse lexically.
     *
     * @param stringToParse
     */
    public LexicalParser(String stringToParse){
        actPos = -1;
        atomicExpression = new ArrayList<String>();
        parse(stringToParse);
    }

    /**
     * It parses lexically this string for atomic expression, lists and operators.
     *
     * @param toParse
     */
    private void parse(String toParse){
        int length = toParse.length();
        char actualChar;
        SpecialChars special = SpecialChars.NONE;
        atomic = "";
        char lastChar = '\0';
        for(int actCharIdx = 0; actCharIdx < length; actCharIdx++){
            actualChar = toParse.charAt(actCharIdx);
            if(actualChar == '#' && special != SpecialChars.QUOTATION_MARK){
                special = SpecialChars.HASHTAG;
            }

            if(isAtomicExpressionFinished(actualChar, lastChar, special)){
                atomicExpression.add(atomic);
                if(!isCharEmptySpace(actualChar) && !isCharParenthesis(actualChar) && actualChar != '\"'){
                    atomic = "" + actualChar;
                } else {
                    atomic = "";
                }
            } else {
                if(special == SpecialChars.QUOTATION_MARK || !isCharEmptySpace(actualChar)){
                    atomic += actualChar;
                }
            }


            if(actualChar == '"'){
                if(special != SpecialChars.QUOTATION_MARK){
                    special = SpecialChars.QUOTATION_MARK;
                } else {
                    special = SpecialChars.NONE;
                }
            }
            lastChar = actualChar;
        }
        if(!atomic.equals("")){
            atomicExpression.add(atomic);
        }
    }

    /**
     * True if the atomic expression is finished.
     *
     * @param actualChar actual character of the String to parse.
     * @param lastChar last character
     * @param special Whether I am in a specific String.
     * @return true if the expression is finished.
     */
    private boolean isAtomicExpressionFinished(char actualChar, char lastChar, SpecialChars special) {
        if(special == SpecialChars.QUOTATION_MARK){
            if(actualChar != '"'){
                return false;
            } else {
                atomic += actualChar;
                return true;
            }
        }

        if(isCharEmptySpace(actualChar) && isCharEmptySpace(lastChar)){
            return false;
        }

        if(actualChar == '('){
            atomic += actualChar;
            return true;
        }

        if(actualChar == ')'){
            if(isCharEmptySpace(lastChar) || lastChar == ')'){
                atomic += actualChar;
                return true;
            } else {
                if(!atomic.equals("")){
                    atomicExpression.add(atomic);
                }

                atomic = "" + actualChar;
                return true;
            }
        }

        if(isCharEmptySpace(actualChar) && !atomic.equals("")){
            return true;
        }

        return false;
    }

    /**
     * True if the character is empty space.
     *
     * @param actChar
     * @return
     */
    private boolean isCharEmptySpace(char actChar){
        return actChar == ' ' || actChar == '\n' || actChar == '\r';
    }

    /**
     * True if the character is left or right bracket
     *
     * @param actChar
     * @return
     */
    private boolean isCharParenthesis(char actChar){
        return actChar == '(' || actChar == ')';
    }

    /**
     * It returns next atomic expression
     *
     * @return next atomic expression or null.
     */
    public String getNextAtomic(){
        actPos++;
        if(actPos < atomicExpression.size()){
            return atomicExpression.get(actPos);
        } else {
            return null;
        }
    }

    /**
     * It returns all atomic expressions.
     *
     * @return  List of all atomics.
     */
    public List<String> getAllAtomics(){
        return atomicExpression;
    }
}
