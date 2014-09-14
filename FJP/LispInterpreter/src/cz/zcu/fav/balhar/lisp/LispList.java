package cz.zcu.fav.balhar.lisp;

import cz.zcu.fav.balhar.lisp.exceptions.MalformedListException;

import java.util.ArrayList;
import java.util.List;

/**
 * It is inner representation of list. I store list as a String in format [item, item, item]
 * LispList takes such a list as parameter and then parses it. If the list is not correct it throws
 * MalformedListException.
 *
 * @author Jakub Balhar
 */
public class LispList {
    private List<String> atoms;

    /**
     * It parses param and creates List of items it contains.
     *
     * @param list String representing the list.
     * @throws MalformedListException The list was incorrect.
     */
    public LispList(String list) throws MalformedListException {
        if(!list.startsWith("[") || !list.endsWith("]")){
            throw new MalformedListException();
        }

        atoms = new ArrayList<String>();
        parseList(list);
    }

    /**
     * It simply lexically parses the list for atoms.
     *
     * @param list list to be parsed.
     */
    private void parseList(String list){
        list = list.substring(1, list.length() - 1);
        boolean ignoreDelimiter = false, ignoreDelimiterList = false;
        String atom = "";
        int length = list.length();
        char actualChar;
        for(int actCharIdx = 0; actCharIdx < length; actCharIdx++){
            actualChar = list.charAt(actCharIdx);
            if(actualChar == ',' && !ignoreDelimiter && !ignoreDelimiterList){
                atoms.add(atom);
                atom = "";
                actCharIdx++;
                continue;
            }
            if(actualChar == '"' && !ignoreDelimiterList) {
                ignoreDelimiter = !ignoreDelimiter;
            }
            if((actualChar == '[' || actualChar == ']') && !ignoreDelimiter) {
                ignoreDelimiterList = !ignoreDelimiterList;
            }

            atom += actualChar;
        }
        if(!atom.equals("")){
            atoms.add(atom);
        }
    }

    /**
     * Simple getter
     *
     * @return List of all Atoms
     */
    public List<String> getAllAtoms(){
        return atoms;
    }

    /**
     * It removes first atom from the list and returns it.
     *
     * @return first atom in the list
     */
    public String removeFirst(){
        return atoms.remove(0);
    }

    /**
     * It removes last atom from the list and returns it.
     *
     * @return last atom in the list
     */
    public String removeLast(){
        return atoms.remove(atoms.size() - 1);
    }

    /**
     * It adds another atom to the end of the list.
     *
     * @param atom atom to add.
     */
    public void addToList(String atom){
        atoms.add(atom);
    }

    /**
     * It adds another atom to the beginning of the list.
     *
     * @param atom atom to add
     */
    public void addToListBeginning(String atom){
        atoms.add(0, atom);
    }

    /**
     * It creates String representation of this List.
     *
     * @return String representation of the list.
     */
    public String toString(){
        String result = "[";
        for(String atom: atoms){
            result += atom + ", ";
        }
        if(atoms.size() > 0){
            result = result.substring(0, result.length() - 2);
        }
        result += "]";
        return result;
    }
}
