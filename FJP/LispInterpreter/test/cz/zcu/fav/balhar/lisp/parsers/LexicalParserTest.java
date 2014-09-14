package cz.zcu.fav.balhar.lisp.parsers;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 22.12.12
 * Time: 10:34
 */
public class LexicalParserTest {
    @Test
    public void testGetNextAtomic() throws Exception {
        LexicalParser parser = new LexicalParser("( car 5 2 )");
        String[] atomics = new String[]{"(","car","5","2",")"};
        int actualPos = 0;
        String actualAtomic;
        while((actualAtomic = parser.getNextAtomic()) != null){
            assertEquals(atomics[actualPos], actualAtomic);
            actualPos++;
        }
        assertEquals(5, actualPos);
    }

    @Test
    public void testGetNextAtomic1() throws Exception {
        LexicalParser parser = new LexicalParser("(car 5 (car 5 2))");
        String[] atomics = new String[]{"(","car", "5", "(", "car", "5", "2", ")", ")"};
        int actualPos = 0;
        String actualAtomic;
        while((actualAtomic = parser.getNextAtomic()) != null){
            assertEquals(atomics[actualPos], actualAtomic);
            actualPos++;
        }
        assertEquals(9, actualPos);
    }

    @Test
    public void testGetNextAtomic2() throws Exception {
        LexicalParser parser = new LexicalParser("(+ 5 (car (- 6 4 ) \"test uvozovek\"))");
        String[] atomics = new String[]{"(","+", "5", "(", "car", "(", "-", "6", "4",")","\"test uvozovek\"",
                ")",")"};
        int actualPos = 0;
        String actualAtomic;
        while((actualAtomic = parser.getNextAtomic()) != null){
            assertEquals(atomics[actualPos], actualAtomic);
            actualPos++;
        }
        assertEquals(13, actualPos);
    }
}
