package db;

import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by benbenzour on 2/26/17.
 */
public class TableTest {

    @Test
    public void testTester() {

        String[] colname = {"dogs", "cats"};
        String[] types = {"float","string"};
        String[] values = {"123e2", "Gary"};
        String[] values1 = {"1.111111", "Bobby"};
        LinkedList<String> c = new LinkedList<>();
        c.add("cats");

        Table t1 = new Table("test", colname, types );
        Table t2 = new Table("test", colname, types );

        assertNotEquals(t1,t2);

        t1.insertLastRow(values);
        t1.insertLastRow(values);
        t1.removeColumn(c);
        System.out.println(t1.print());
        t1.removeRow(0);
        t1.removeRow(0);

        String r = t1.getRowString(1);

        System.out.println(r);


        System.out.println("The Numbers of Columns: " + t1.colSize());
        System.out.println("The Numbers of Type: " + types.length);

        System.out.println();

        String s = t1.getTable();
        System.out.println(s);
    }


}
