package cz.zcu.fav.balhar.lisp;

import cz.zcu.fav.balhar.lisp.exceptions.NonExistingOperatorException;
import cz.zcu.fav.balhar.lisp.exceptions.WrongAmountOfParameters;
import cz.zcu.fav.balhar.lisp.exceptions.WrongParameterTypesException;
import cz.zcu.fav.balhar.lisp.parsers.SyntaxParser;

import java.io.*;

/**
 * Main class of the application. It reads file passed as parameter and evaluates it and write the result
 * to standard output.
 *
 * @author Jakub Balhar
 */
public class Reader {
    private File file;
    private SyntaxParser parser;

    /**
     *
     * @param file path to file to read from.
     */
    public Reader(String file){
        this.file = new File(file);
        this.parser = new SyntaxParser();
    }

    /**
     * It evaluates expression from the File and returns the result. Which means either correct result
     * or some of the errors.
     *
     * @return Evaluated Lisp expression.
     */
    public String evaluate(){
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader fr = new BufferedReader(new FileReader(file));
            String line = "";
            while((line = fr.readLine()) != null){
                sb.append(line);
            }
        } catch (FileNotFoundException e) {
            return "The file is invalid or does not exists.";
        } catch (IOException e) {
            return "The file is invalid.";
        }
        String result;
        try {
            result = parser.evaluate(sb.toString().trim());
        } catch (WrongAmountOfParameters wrongAmountOfParameters) {
            return "You give incorrect amount of parameters.";
        } catch (NonExistingOperatorException e) {
            return "The operator you tried to use does not exists.";
        } catch (WrongParameterTypesException e) {
            return "Some of parameters were of a wrong type.";
        } catch (Exception ex){
            return "Incorrect syntax.";
        }
        return result;
    }

    public static void main(String[] args){
        if(args.length < 1){
            System.out.println("Correct usage is java Reader {fileToInterpret}");
            System.exit(3);
        }

        Reader reader = new Reader(args[0]);
        System.out.println(reader.evaluate());
    }
}
