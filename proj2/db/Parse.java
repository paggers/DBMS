package db;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parse {
    // Various common constructs, simplifies parsing.
    private static final String REST = "\\s*(.*)\\s*",
            COMMA = "\\s*,\\s*",
            AND = "\\s+and|AND\\s+",
            NAME = "\\s*(\\w+)\\s*",
            CLEANME = "\\s*'(.*?)'\\s*",
            SPACE = "\\s*",
            COMPARISON = "(\\w+)\\s*([\\-*/'<>=!]{1,2})\\s*('(.*?)'|\\w+|\\d+)*",
            ARITHMETIC = "(\\w+)\\s*([-+*/]{1})\\s*(\\w+)";
    // Stage 1 syntax, contains the command name.
    private static final Pattern CREATE_CMD = Pattern.compile(SPACE + "create table " + REST),
            LOAD_CMD = Pattern.compile(SPACE + "load " + NAME),
            STORE_CMD = Pattern.compile(SPACE + "store " + NAME),
            DROP_CMD = Pattern.compile(SPACE + "drop table " + NAME),
            INSERT_CMD = Pattern.compile(SPACE + "insert into " + REST),
            PRINT_CMD = Pattern.compile(SPACE + "print " + NAME),
            SELECT_CMD = Pattern.compile(SPACE + "select " + REST),
            COMP_CMD = Pattern.compile(SPACE + COMPARISON + REST),
            ARITH_CMD = Pattern.compile(ARITHMETIC),
            NAME_CMD = Pattern.compile(CLEANME);
    // Stage 2 syntax, contains the clauses of commands.
    private static final Pattern CREATE_NEW = Pattern.compile("(\\S+)\\s+\\((\\S+\\s+\\S+\\s*"
            + "(?:,\\s*\\S+\\s+\\S+\\s*)*)\\)"),
            SELECT_CLS = Pattern.compile("([^,]+?(?:,[^,]+?)*)\\s+from\\s+"
                    + "(\\S+\\s*(?:,\\s*\\S+\\s*)*)(?:\\s+where\\s+"
                    + "([\\w\\s+\\-*/'<>=!]+?(?:\\s+and\\s+"
                    + "[\\w\\s+\\-*/'<>=!]+?)*))?"),
            CREATE_SEL = Pattern.compile("(\\S+)\\s+as select\\s+"
                    + SELECT_CLS.pattern()),
            COL_SEL = Pattern.compile("(.*)\\s+as\\s*(\\w+)\\s*"),
            INSERT_CLS = Pattern.compile("(\\S+)\\s+values\\s+(.+?"
                    + "\\s*(?:,\\s*.+?\\s*)*)");
    private Database db;
    private Operation o;

    //    Parse constructor
    Parse(Database d) {
        db = d;
        o = new Operation(d);
    }

    //    Cleans up the '' signs while loading strings from a table
    static String nameClean(String name) {
        Matcher m;
        if ((m = NAME_CMD.matcher(name)).matches()) {
            return m.group(1);
        }
        return name;
    }

    //    String evaluating function
    String eval(String query) {
        Matcher m;
        if ((m = CREATE_CMD.matcher(query)).matches()) {
            return createTable(m.group(1));
        } else if ((m = LOAD_CMD.matcher(query)).matches()) {
            return loadTable(m.group(1));
        } else if ((m = STORE_CMD.matcher(query)).matches()) {
            return storeTable(m.group(1));
        } else if ((m = DROP_CMD.matcher(query)).matches()) {
            return dropTable(m.group(1));
        } else if ((m = INSERT_CMD.matcher(query)).matches()) {
            return insertRow(m.group(1));
        } else if ((m = PRINT_CMD.matcher(query)).matches()) {
            return printTable(m.group(1));
        } else if ((m = SELECT_CMD.matcher(query)).matches()) {
            return select(m.group(1));
        }
        return "ERROR: Command not legible";
    }

    private String createTable(String expr) {
        Matcher m;
        if ((m = CREATE_NEW.matcher(expr)).matches()) {
            return createNewTable(m.group(1), m.group(2).split(COMMA));
        } else if ((m = CREATE_SEL.matcher(expr)).matches()) {
            if (m.group(4) != null) {
                return createSelectedTable(m.group(1), m.group(2).split(COMMA),
                        m.group(3).split(COMMA), m.group(4).split(AND));

            } else {
                return createSelectedTable(m.group(1), m.group(2).split(COMMA),
                        m.group(3).split(COMMA), null);

            }
        }
        return "ERROR: Malformed create";

    }

    private String createNewTable(String name, String[] cols) {
//        String parsing each element of cols[] by whitespace
        String[] colNames = new String[cols.length];
        String[] colType = new String[cols.length];
        String delims = "[ ]+";
        String[] tokens = new String[2];
        for (int i = 0; i < cols.length; i++) {
            tokens = cols[i].split(delims);
            colNames[i] = tokens[0];
            colType[i] = tokens[1];
        }
//        invoking createNewTable function with table name, column names[], column types[]
        return db.createNewTable(name, colNames, colType);
    }

    //    Selected tables: look for as\math ops\
    private String createSelectedTable(String name, String[] exprs,
                                       String[] tables, String[] conds) {
        return db.createSelectedTable(name, exprs, tables, conds);
    }

    private String loadTable(String name) {
        return db.loadTable(name);
    }

    private String storeTable(String name) {
        return db.storeTable(name);
    }

    private String dropTable(String name) {
        return db.dropTable(name);
    }

    private String insertRow(String expr) {
        Matcher m = INSERT_CLS.matcher(expr);
        if (!m.matches()) {
            return "ERROR: Malformed insert";
        }

        return db.insertRow(m.group(1), m.group(2).split(COMMA));
    }

    private String printTable(String name) {
        return db.printTable(name);
    }

    private String select(String expr) {
        Matcher m;
        if ((m = SELECT_CLS.matcher(expr)).matches()) {
            if (m.group(3) == null) {
                return db.select("temp", m.group(1).split(COMMA),
                        m.group(2).split(COMMA), null);
            }
            return db.select("temp", m.group(1).split(COMMA),
                    m.group(2).split(COMMA), m.group(3).split(AND));
        }
        return "ERROR: Malformed select";
    }

    //    Checks comparison operations inside select/create
    LinkedList<LinkedList> compOp(String[] cond) {
        Matcher m;
        LinkedList<String> op = new LinkedList<>();
        LinkedList<String> lhs = new LinkedList<>();
        LinkedList<String> rhs = new LinkedList<>();
        for (int i = 0; i < cond.length; i++) {
            if ((m = COMP_CMD.matcher(cond[i])).matches()) {
                lhs.addLast(m.group(1));
                op.addLast(m.group(2));
                rhs.addLast(m.group(3));
            } else {
                return null;
            }
        }
        LinkedList<LinkedList> result = new LinkedList<>();
        result.addLast(rhs);
        result.addLast(lhs);
        result.addLast(op);
        return result;
    }

    //    Checks arithmetic operations inside select statements
    LinkedList<LinkedList> arithOp(String[] cond) {
        Matcher m;
//        Linked List to store the columns which need to be operated on
        LinkedList<String> colNames = new LinkedList<>();
        LinkedList<String> temp = new LinkedList<>();
//        Checking for column conditions
        for (int i = 0; i < cond.length; i++) {
            m = COL_SEL.matcher(cond[i]);
            if (m.matches()) {
                colNames.addLast(m.group(2));
                temp.addLast(m.group(1));
            }
        }
        if (colNames.size() == 0) {
            return null;
        }
//        storing lhs, rhs columns, and arithmetic operator
        LinkedList<String> op = new LinkedList<>();
        LinkedList<String> lhs = new LinkedList<>();
        LinkedList<String> rhs = new LinkedList<>();
        for (String c : temp) {
            if ((m = ARITH_CMD.matcher(c)).matches()) {
                lhs.addLast(m.group(1));
                op.addLast(m.group(2));
                rhs.addLast(m.group(3));
            } else {
                return null;
            }
        }
        LinkedList<LinkedList> result = new LinkedList<>();
        result.addLast(colNames);
        result.addLast(lhs);
        result.addLast(rhs);
        result.addLast(op);
        return result;
    }

    //    Returns the end product's columns
    LinkedList<String> realCols(String[] cond) {
        Matcher m;
//        Linked List to store the columns which do NOT need to be operated on
        LinkedList<String> colNames = new LinkedList<>();
//        Checking for column conditions
        for (int i = 0; i < cond.length; i++) {
            m = COL_SEL.matcher(cond[i]);
            if (!m.matches()) {
                colNames.addLast(cond[i]);
            } else {
                colNames.addLast(m.group(2));
            }
        }
        return colNames;
    }
}


