package db;


import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.util.NoSuchElementException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.io.FileReader;
import java.io.BufferedReader;



/**
 * Variables:
 * size
 * tables
 * parser
 * Methods:
 * createNewTable
 * createSelectedTable
 * loadTable
 * storeTable
 * dropTable
 * insertRow
 * printTable
 * select
 * Constrictions:
 * Notes:
 */
public class Database {


    int size;
    HashMap<String, Table> tables;
    Parse p;

    /**
     * either hashMap, array, or a set to store the tables names
     * was thinking of an array since we want it in order
     */
    public Database() {
        size = 0;
        tables = new HashMap();
        p = new Parse(this);
    }

    public String transact(String query) {
        return p.eval(query);
    }


    /** Creates a table from scratch:
     * "create table <table name> (<column0 name> <type0>, <column1 name> <type1>, ...)"*/
    String createNewTable(String name,
                          String[] colNames, String[] colTypes) {

        try {
            if (tables.get(name) != null) {
                return "ERROR: Table already exists";

            }
            Table t = new Table(name, colNames, colTypes);
            tables.put(name, t);

            return "";
        } catch (NullPointerException e) {
            return "ERROR: Malformed Table";
        } catch (IllegalArgumentException e) {
            return "ERROR: Malformed Table";
        }


    }

    /**Creates a Table using select: "create table <table name> as <select clause>"*/
    String createSelectedTable(String name, String[] colNames,
                               String[] selectedTables, String[] cond) {

        try {
            if (tables.get(name) != null) {
                return "ERROR: Table already exists";
            }

            return select(name, colNames, selectedTables, cond);
        } catch (ArithmeticException e) {
            return "ERROR: Malformed Table";
        } catch (NullPointerException e) {
            return "ERROR: Malformed Table";
        }
    }

    /**Loads a Table from .tbl file: "load <table name>"*/
    String loadTable(String name) {


        String l;
        String[] line;
        String[] colNames;
        String[] colTypes;
        String[] values;


        try {

            if (tables.get(name) != null) {
                tables.remove(name);
            }
            FileReader fr = new FileReader(name + ".tbl");
            BufferedReader bf = new BufferedReader(fr);


            if ((l = bf.readLine()) != null) {
                line = l.split("\\s*(,|\\s)\\s*");
            } else {
                return "ERROR: Empty Table";
            }
            colNames = new String[line.length / 2];
            colTypes = new String[line.length / 2];

            int j = 0, k = 0;

            for (int i = 0; i < line.length; i += 1) {
                if (i % 2 == 0) {
                    colNames[j] = line[i];
                    j += 1;
                } else {
                    colTypes[k] = line[i];
                    k += 1;
                }

            }

            Table t = new Table(name, colNames, colTypes);
            tables.put(name, t);

            while ((l = bf.readLine()) != null) {
                values = l.split("\\s*(,)\\s*");
                t.insertLastRow(values);
            }

            bf.close();
            return "";

        } catch (NullPointerException e) {
            return "ERROR: " + e;
        } catch (NumberFormatException e) {
            return "ERROR: " + e;
        } catch (IllegalArgumentException e) {
            return "ERROR: " + e;
        } catch (IOException e) {
            return "ERROR: " + e;
        } 
    }


    /**Stores a Table in a .tbl file: "store <table name>"*/
    String storeTable(String name) {
        PrintWriter writer = null;
        Table t = tables.get(name);

        try {
            /** Boolean decides if to overwrite the file if false, or continue if true */
            FileWriter fw = new FileWriter(name + ".tbl", false);
            writer = new PrintWriter(fw);
            writer.println(t.getColString());
            int i = 1;
            for (; i < t.rowSize(); i += 1) {
                writer.println(t.getRowString(i));
            }
            writer.print(t.getRowString(i));
            writer.close();
            return "";

        } catch (IOException e) {
            return "ERROR: Invalid Table";
        } catch (NullPointerException e) {
            return "ERROR: Invalid Table";
        }


    }

    /**Delete a Table from Database: "drop table <table name>"*/
    String dropTable(String name) {


        try {
            if (tables.get(name).equals(null)) {
                return "ERROR: Table Does Not Exist";
            }
            tables.remove(name);
            return "";
        } catch (NoSuchElementException e) {
            return "ERROR: Delete operation failed.";

        } catch (DirectoryIteratorException e) {
            return "ERROR: Delete operation failed.";
        } catch (NullPointerException e) {
            return "ERROR: Delete operation failed.";
        }

    }


    /**Inserts a row to a specific table:
     *  "insert into <table name> values <literal0>,<literal1>,..."*/
    String insertRow(String name, String[] expr) {

        try {
            if (Arrays.asList(expr).contains("NaN")) {
                throw new NumberFormatException("NaN Value");
            }
            Table t = tables.get(name);
            t.insertLastRow(expr);

        } catch (NullPointerException e) {
            return "ERROR: Invalid Number Of Values";
        } catch (NumberFormatException e) {
            return "ERROR: " + e;
        }

        return "";

    }

    /**Prints a Table: "print <table name>"*/
    String printTable(String name) {

        try {
            return tables.get(name).getTable();
        } catch (IndexOutOfBoundsException e) {
            return "ERROR: Cant print table";
        } catch (NullPointerException e) {
            return "ERROR: Cant print table";
        }
    }


    /**  Selects function: "select <column expr0>,<column expr1>,
     * ... from <table0>,<table1>,... where <cond0> and <cond1> and ..."*/
    String select(String name, String[] columns, String[] tablesList, String[] conds) {

        try {
            for (String tableName : tablesList) {

                if (tables.get(tableName) == null) {
                    return "ERROR: Table does not exist";
                }
            }
            //Regular Select or Conditioned Selected? Pass to a new function accordingly
            if (conds != null) {
                Table t = (condSelect(name, columns, tablesList, conds));
                if (name.equals("temp")) {
                    tables.remove(name);
                    return t.print();
                } else {
                    tables.put(name, t);
                }
                return "";
            }
            Table t = (regularSelect(name, columns, tablesList));
            if (name.equals("temp")) {
                tables.remove(name);
                return t.print();
            } else {
                tables.put(name, t);
            }
            return "";
        } catch (IndexOutOfBoundsException e) {
            return "ERROR:" + e;
        } catch (ArithmeticException e) {
            return "ERROR: " + e;
        }
        //        Check if tables exist


    }

    /**Conditioned Select Statement - handles WHERE clauses*/
    Table condSelect(String name, String[] columns, String[] tablesList, String[] conds) {
        Table t1;
        Table t2;
        tablesList = new LinkedHashSet<String>(Arrays.asList(tablesList)).toArray(new String[0]);

        //Parse Comparison and Arithmetic Operations
        LinkedList<LinkedList> compOp = p.compOp(conds);
        LinkedList<LinkedList> arithOp = p.arithOp(columns);

        //Check if Comparison operation exist in the statement -- if null should return ERROR
        if (compOp == null) {
            return null;
        }
        //Unwrap Comparison information
        LinkedList<String> rhsComp = compOp.get(0);
        LinkedList<String> lhsComp = compOp.get(1);
        LinkedList<String> operationComp = compOp.get(2);
        //Check if arithemtic operations exist in the statement
        if (arithOp != null) {
            LinkedList<String> columnsToKeep = p.realCols(columns);
            LinkedList<String> colNames = arithOp.get(0);
            LinkedList<String> lhs = arithOp.get(1);
            LinkedList<String> rhs = arithOp.get(2);
            LinkedList<String> operators = arithOp.get(3);

            // creates a new table from all columns - before running operators
            t1 = tables.get(tablesList[0]);
            t1 = selectColumns(name, tablesList[0], t1.getColNames());

            for (int i = 1; i < tablesList.length; i++) {
                t2 = tables.get(tablesList[i]);
                t2 = selectColumns(name, tablesList[i], t2.getColNames());
                t1 = joinTwoTables(name, t1, t2);
                tables.put(t1.getName(), t1);
            }
            /**run over the arithOp Columns that needs to be handeled
             *  - t1 is the new combined table! And inserts the new column.*/
            for (int i = 0; i < operators.size(); i += 1) {
                t1.insertNewColumn(t1.arithmaticOperator(colNames.get(i),
                        lhs.get(i), rhs.get(i), operators.get(i)));
            }
            t1.removeColumn(columnsToKeep);
            for (int i = 0; i < operationComp.size(); i++) {

                t1.comparisonOperation(lhsComp.get(i), rhsComp.get(i), operationComp.get(i));
            }
            return t1;
        }
        if (columns[0].equals("*")) {
            t1 = tables.get(tablesList[0]);
            t1 = selectColumns(name, tablesList[0], t1.getColNames());

            for (int i = 1; i < tablesList.length; i++) {
                t2 = tables.get(tablesList[i]);
                t2 = selectColumns(name, tablesList[i], t2.getColNames());
                t1 = joinTwoTables(name, t1, t2);
                tables.put(t1.getName(), t1);
            }
            for (int i = 0; i < operationComp.size(); i++) {
                t1.comparisonOperation(lhsComp.get(i), rhsComp.get(i), operationComp.get(i));
            }
            return t1;
            //Check similar and distinct column names of tables
        } else {
            t1 = tables.get(tablesList[0]);
            t1 = selectColumns("temp", tablesList[0], t1.getColNames());


            for (int i = 1; i < tablesList.length; i++) {
                t2 = tables.get(tablesList[i]);
                t2 = selectColumns("temp", tablesList[i], t2.getColNames());
                t1 = joinTwoTables("temp", t1, t2);
                tables.put(t1.getName(), t1);
            }
            for (int i = 0; i < operationComp.size(); i++) {
                t1.comparisonOperation(lhsComp.get(i), rhsComp.get(i), operationComp.get(i));
            }
            return selectColumns(name, t1.getName(), columns);
        }
    }

    /**Regular Select Statement - handles without WHERE clause*/
    Table regularSelect(String name, String[] columns, String[] tablesList) {
        Table t1;
        Table t2;
        tablesList = new LinkedHashSet<String>(Arrays.asList(tablesList)).toArray(new String[0]);

        //Parse Arithmetic Op
        LinkedList<LinkedList> arithOp = p.arithOp(columns);

        // creates a new table from all columns - before running operators
        if (arithOp != null) {
            //get real column names, operated columns, and operation
            LinkedList<String> columnsToKeep = p.realCols(columns);
            LinkedList<String> colNames = arithOp.get(0);
            LinkedList<String> lhs = arithOp.get(1);
            LinkedList<String> rhs = arithOp.get(2);
            LinkedList<String> operators = arithOp.get(3);

            t1 = tables.get(tablesList[0]);
            t1 = selectColumns(name, tablesList[0], t1.getColNames());

            for (int i = 1; i < tablesList.length; i++) {
                t2 = tables.get(tablesList[i]);
                t2 = selectColumns(name, tablesList[i], t2.getColNames());
                t1 = joinTwoTables(name, t1, t2);
                tables.put(t1.getName(), t1);

            }

            /**run over the arithOp Columns that needs to be handeled
             * - t1 is the new combined table! And inserts the new column.*/
            for (int i = 0; i < operators.size(); i += 1) {
                t1.insertNewColumn(t1.arithmaticOperator(colNames.get(i),
                        lhs.get(i), rhs.get(i), operators.get(i)));
            }
            t1.removeColumn(columnsToKeep);
            return t1;
        }

        if (columns[0].equals("*")) {
            t1 = tables.get(tablesList[0]);
            t1 = selectColumns(name, tablesList[0], t1.getColNames());

            for (int i = 1; i < tablesList.length; i++) {
                t2 = tables.get(tablesList[i]);
                t2 = selectColumns(name, tablesList[i], t2.getColNames());
                t1 = joinTwoTables(name, t1, t2);
                tables.put(t1.getName(), t1);
            }

            return t1;
            //        Check similar and distinct column names of tables
        } else {
            t1 = tables.get(tablesList[0]);
            t1 = selectColumns(name, tablesList[0], columns);
            for (int i = 1; i < tablesList.length; i++) {
                t2 = tables.get(tablesList[i]);
                t2 = selectColumns(name, tablesList[i], columns);
                t1 = joinTwoTables(name, t1, t2);
                tables.put(t1.getName(), t1);
            }

            return t1;
        }

    }

    Table joinTwoTables(String name, Table t1, Table t2) {
        //Check similar columns and log them in simNames
        if (t1.equals(t2)) {
            return t1;
        }
        int sim = 0;
        LinkedList<String> simNames = new LinkedList<>();
        for (String c1 : t1.getColNames()) {
            for (String c2 : t2.getColNames()) {
                if (c1.equals(c2)) {
                    sim += 1;
                    simNames.addLast(c1);
                }
            }
        }
        if (sim == 0) {
            return noCommonJoin(name, t1, t2);
        }
        //Check matches between similar columns and log the row number per table
        LinkedList<Integer> simRowsT1 = new LinkedList<>();
        HashMap<Integer, LinkedList> t1ToT2 = new HashMap<>();
        LinkedList<LinkedList> totalSimRows = new LinkedList<>();

        checkSimColumns(t1, t2, simNames, t1ToT2, simRowsT1, totalSimRows);
        boolean flag = false;

        /**Here we check if there is a repetition
         *  of the same row equal to the number of similar columns*/
        simRowsT1 = totalSimRows.get(0);
        LinkedHashSet<Integer> similarRow = new LinkedHashSet<>();
        LinkedList<Integer> simRowsTemp;
        LinkedList<Integer> simRowsFinal = new LinkedList<>();
        if (totalSimRows.size() > 1) {
            for (int i = 1; i < totalSimRows.size(); i++) {
                simRowsTemp = totalSimRows.get(i);
                int count = 0;
                for (int j = 0; j < simRowsT1.size(); j++) {
                    Integer intTemp = simRowsT1.get(j);
                    for (int k = 0; k < simRowsTemp.size(); k++) {
                        if (intTemp == simRowsTemp.get(k)) {
                            count += 1;
                        }
                        if (count == sim) {
                            similarRow.add(intTemp);
                            break;
                        }
                    }
                }
            }
        } else {
            simRowsFinal = simRowsT1;
        }
        simRowsFinal.addAll(0, similarRow);
        /**Call createSubTable with similar column & rows to generate similarity subTable*/
        Table simTable = t1.similarSubTable(simNames, simRowsFinal);
        /**Call createSubTable with unsimilar column & rows to generate unsimilar subTable from t1*/
        Table unsimT1 = t1.unsimSubTable(simNames, simRowsFinal);
        /**Call createSubTable with unsimilar column & rows to generate unsimilar subTable from t2*/
        if (!flag) {
            for (int i = 0; i < simRowsFinal.size(); i++) {
                simRowsFinal.addLast(
                        (Integer) (t1ToT2.get(simRowsFinal.removeFirst())).removeFirst());
            }
        } else {
            int row;
            int size1 = simRowsFinal.size();
            int size2;
            for (int i = 0; i < size1; i++) {
                row = simRowsFinal.removeFirst();
                size2 = (t1ToT2.get(row).size());
                for (int j = 0; j < size2; j++) {
                    simRowsFinal.addLast((Integer) (t1ToT2.get(row).removeFirst()));
                }
            }
        }
        Table unsimT2 = t2.unsimSubTable(simNames, simRowsFinal);
        simTable = simTable.naiveJoinTables(unsimT1);
        return simTable.naiveJoinTables(unsimT2);
    }

    /**handles select columns clause, acts as a dispatcher*/
    Table selectColumns(String desTable, String srcTable, String[] columns) {

        LinkedList<String> newTypes = new LinkedList();
        LinkedList<String> newNames = new LinkedList();
        Table t = tables.get(srcTable);

        for (int i = 0; i < columns.length; i += 1) {
            if (t.getTypeByName(columns[i]) != null && t.getColByName(columns[i]) != null) {
                newTypes.add(t.getTypeByName(columns[i]));
                newNames.add(t.getColByName(columns[i]));
            }
        }
        String[] nT = newTypes.toArray(new String[newTypes.size()]);
        String[] nN = newNames.toArray(new String[newNames.size()]);

        Table temp = new Table(desTable, nN, nT);
        tables.put(desTable, temp);
        for (int i = 0; i < newNames.size(); i += 1) {
            temp.insertColumn(t.getColumnCopy(newNames.get(i)));
        }
        return temp;
    }

    /**creates a new table in cartesian product*/
    Table noCommonJoin(String name, Table t1, Table t2) {
        {
            String[] names = new String[t1.colSize() + t2.colSize()];
            String[] types = new String[t1.colSize() + t2.colSize()];

            System.arraycopy(t1.getColNames(), 0, names, 0, t1.colSize());
            System.arraycopy(t2.getColNames(), 0, names, t1.colSize(), t2.colSize());
            System.arraycopy(t1.getColTypes(), 0, types, 0, t1.colSize());
            System.arraycopy(t2.getColTypes(), 0, types, t1.colSize(), t2.colSize());

            Table t = new Table(name, names, types);

            for (int i = 0; i < t1.rowSize(); i += 1) {
                for (int j = 0; j < t2.rowSize(); j += 1) {
                    t.insertLastRow(t1.combineRows(t2, i, j));
                }
            }
            return t;
        }
    }

    /**checks what are the similar columns between 2 tables*/
    void checkSimColumns(Table t1, Table t2, LinkedList<String> simNames, HashMap<Integer,
            LinkedList> t1ToT2, LinkedList<Integer> simRowsT1,
                                LinkedList<LinkedList> totalSimRows) {
        boolean flag = false;
        LinkedList<Integer> temp;
        for (String cn : simNames) {
            String[] t1Col = t1.getCol(cn);
            String[] t2Col = t2.getCol(cn);
            for (int i = 0; i < t1Col.length; i++) {
                for (int j = 0; j < t2Col.length; j++) {
                    if (t1Col[i].equals(t2Col[j])) {
                        simRowsT1.addLast(i);
                        if (t1ToT2.containsKey(i)) {
                            temp = t1ToT2.get(i);
                            temp.addLast(j);
                            t1ToT2.put(i, temp);
                            flag = true;
                        } else {
                            temp = new LinkedList<>();
                            temp.addLast(j);
                            t1ToT2.put(i, temp);
                        }
                    }
                }
            }
            totalSimRows.addLast(simRowsT1);
        }
    }

}
