package cz.zcu.fav.balhar.lisp;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 25.12.12
 * Time: 10:04
 */
public class LispListTest {
    @Test
    public void testList() throws Exception {
        LispList list = new LispList("[]");
        assertEquals("[]", list.toString());
    }

    @Test
    public void testList2() throws Exception {
        LispList list = new LispList("[4]");
        assertEquals("[4]", list.toString());
    }

    @Test
    public void testList3() throws Exception {
        LispList list = new LispList("[4, 5]");
        assertEquals("[4, 5]", list.toString());
    }

    @Test
    public void testList4() throws Exception {
        LispList list = new LispList("[4, 5, \" test d\"]");
        assertEquals("[4, 5, \" test d\"]", list.toString());
    }

    @Test
    public void testList5() throws Exception {
        LispList list = new LispList("[4, 5, [4, 5], \" tre []\"]");
        assertEquals("[4, 5, [4, 5], \" tre []\"]", list.toString());
    }
}
