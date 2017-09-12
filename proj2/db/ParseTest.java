package db;

import org.junit.Test;

import static org.junit.Assert.assertNotEquals;

/**
 * Created by pagi on 3/1/17.
 */
public class ParseTest {
    @Test
    public void parsetTester() {

        Database db = new Database();
        Parse p = new Parse(db);

//        String str = p.eval("insert into lol values rofl");
//        String str = p.eval("create table seasonRatios as select City,Season,Wins/Losses as Ratio from teams,records where x>y");
//        String str1 = p.eval("load t1");
//        String str2 = p.eval("load t2");

        String str = p.eval("select * from t1, t2 where t1>t2");

    }


}
