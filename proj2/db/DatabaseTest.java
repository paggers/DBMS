package db;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by benbenzour on 2/28/17.
 */
public class DatabaseTest {

    /**
     * Created by benbenzour on 2/26/17.
     */

    @Test
    public void testTester() {

        Database db = new Database();



        db.loadTable("t4");
        db.loadTable("records");
        db.loadTable("fans");
        db.printTable("fans");
        String[] s = {"City","Season","Wins/Losses"};
        String[] b = {"teams","records}"};



        String[] tables = {"t1"};
        String[] expr = {"*"};
        String[] cond = {""};

        db.condSelect("seasonRatios",s,b,cond);

        db.select("temp",expr, tables, cond );

        db.storeTable("test");
//
        //db.createNewTable("test", colname, types );

//        db.insertRow("test", values);
//        db.insertRow("test", values1);
//        db.insertRow("test", values1);
//        db.insertRow("test", values);
//        db.storeTable("test");
//        db.insertRow("test", values);
//        db.storeTable("test");


//        db.createNewTable("test", colname, types );


    }



}
