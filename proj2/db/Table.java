package db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;


/**
 * Variables: columns, # of rows, # of columns, tables name
 * Methods:
 * Constrictions: cant create a table with no cols, cant create an existing table
 * Notes:
 */
class Table {

    static final String NOVALUE = "NOVALUE";
    static final String NAN = "NaN";
    private Column[] cols;
    /**
     * name and index_order HashMap that relates a name of column to its order in the table.
     */
    private HashMap<String, Integer> indexer;
    private int rows;

    private String tableName;
    private String[] colNames;
    private String[] colTypes;

    /**
     * Creates a new table
     */
    Table(String name, String[] colName, String[] type) {

        tableName = name;
        this.colNames = colName;
        this.colTypes = type;
        rows = 0;
        cols = new Column[type.length];
        indexer = new HashMap();

        for (int i = 0; i < colTypes.length; i += 1) {
            String t = colTypes[i];
            if (!t.equals("int") && !t.equals("string") && !t.equals("float")) {
                throw new IllegalArgumentException("Malformed Type");
            }
        }

        for (int i = 0; i < type.length; i += 1) {
            cols[i] = new Column(colName[i], type[i]);
            indexer.put(colName[i], i);
        }


    }


    /** Parser must make sure the number of values corresponds to the # of columns*/
    /**
     * Insert a row to the end of the table
     */
    void insertLastRow(String[] vals) {

        try {
            for (int i = 0; i < cols.length; i += 1) {
                cols[i].insertItem(vals[i]);
            }
        } catch (ArithmeticException e) {
            System.err.println("ERROR: NAN value cannot be inserted");
        }

        rows += 1;

    }

    /**removes a row from a table*/
    void removeRow(int index) {
        try {
            for (int i = 0; i < cols.length; i += 1) {
                cols[i].removeItem(index);
            }
            rows -= 1;
        } catch (NullPointerException e) {
            System.out.println("Invalid number index");
        }
    }

    /**removes a column from a table,
     *  by iterating over the one's that should be kept*/
    void removeColumn(LinkedList<String> columnsToKeep) {

        String[] newNames = new String[columnsToKeep.size()];
        String[] newTypes = new String[columnsToKeep.size()];
        Column[] newColumns = new Column[columnsToKeep.size()];
        int count = 0;


        for (int i = 0; i < cols.length; i += 1) {
            for (int j = 0; j < columnsToKeep.size(); j += 1) {
                if (getColNames()[i].equals(columnsToKeep.get(j))) {
                    newNames[count] = getColNames()[i];
                    newTypes[count] = getColTypes()[i];
                    newColumns[count] = cols[indexer.get(getColNames()[i])];
                    count += 1;
                }
            }
        }
        cols = newColumns;
        colTypes = newTypes;
        colNames = newNames;

        HashMap<String, Integer> newIndexer = new HashMap<>();
        for (int k = 0; k < newColumns.length; k += 1) {
            newIndexer.put(newNames[k], k);
        }
        indexer = newIndexer;


    }


    int colSize() {
        return cols.length;
    }

    int rowSize() {
        return rows;
    }

    String[] getColNames() {
        return colNames;
    }

    String[] getColTypes() {
        return colTypes;
    }

    String getTypeByName(String name) {
        try {
            return colTypes[indexer.get(name)];
        } catch (NullPointerException e) {
            return null;
        }

    }

    String getColByName(String name) {
        try {
            return colNames[indexer.get(name)];
        } catch (NullPointerException e) {
            return null;
        }

    }

    String getName() {
        return tableName;
    }


    String getRowString(int index) {

        String row = "";


        int i = 0;

        for (; i < colSize() - 1; i += 1) {

            row += stringConverter(cols[i], index) + ",";
        }

        row += stringConverter(cols[i], index);

        return row;


    }

    String[] getRow(int index) {

        String[] row = new String[cols.length];

        if (index > rows) {
            throw new NullPointerException("No such row");
        }

        for (int i = 0; i < colSize(); i += 1) {

            row[i] = (String) cols[i].getItem(index);
        }

        return row;
    }

    /**returns a table with 2 tables similar columns*/
    Table similarSubTable(LinkedList<String> columns, LinkedList<Integer> rowIndex) {

        //get the types corresponding types to names.
        String[] types = new String[columns.size()];
        for (int i = 0; i < columns.size(); i += 1) {
            types[i] = getType(columns.get(i));
        }

        String[] array = columns.toArray(new String[columns.size()]);

        Table t = new Table("temp", array, types);
        String[] vals = new String[columns.size()];

        for (int i = 0; i < rowIndex.size(); i += 1) {
            vals = getSelectedRows(columns, rowIndex.get(i));
            t.insertLastRow(vals);
        }

        return t;
    }


    /**returns a table with uncommon columns*/
    Table unsimSubTable(LinkedList<String> similarCols, LinkedList<Integer> rowIndex) {

        //get the types corresponding types to names.
        LinkedList<String> types = new LinkedList();
        LinkedList<String> names = new LinkedList();
        boolean flag = true;

        for (int i = 0; i < cols.length; i += 1) {
            for (int j = 0; j < similarCols.size(); j += 1) {
                if (getColNames()[i].equals(similarCols.get(j))) {
                    flag = false;
                    break;
                }
            }

            if (flag) {
                types.add(getColTypes()[i]);
                names.add(getColNames()[i]);
            }
            flag = true;
        }


        String[] array = names.toArray(new String[names.size()]);
        String[] array1 = types.toArray(new String[names.size()]);

        Table t = new Table("temp", array, array1);
        String[] vals = new String[names.size()];

        for (int i = 0; i < rowIndex.size(); i += 1) {
            vals = getSelectedRows(names, rowIndex.get(i));
            t.insertLastRow(vals);
        }

        return t;
    }

    /**returns a string of items in a selected row and selected columns*/
    String[] getSelectedRows(LinkedList<String> columns, int rowIndex) {

        String[] vals = new String[columns.size()];

        for (int i = 0; i < columns.size(); i += 1) {
            vals[i] = (String) getColumnCopy(columns.get(i)).getItem(rowIndex);
        }
        return vals;
    }

    //joins a new tables that assumes that the columns are the same length
    Table naiveJoinTables(Table t2) {
        String[] names = new String[this.colSize() + t2.colSize()];
        String[] types = new String[this.colSize() + t2.colSize()];

        System.arraycopy(this.getColNames(), 0, names, 0, this.colSize());
        System.arraycopy(t2.getColNames(), 0, names, this.colSize(), t2.colSize());
        System.arraycopy(this.getColTypes(), 0, types, 0, this.colSize());
        System.arraycopy(t2.getColTypes(), 0, types, this.colSize(), t2.colSize());

        Table t = new Table("temp", names, types);

        for (int i = 0; i < this.rowSize(); i += 1) {
            t.insertLastRow(this.combineRows(t2, i, i));

        }
        return t;
    }



    /**This functions returns all rows of a specific column*/
    protected String[] getCol(String colName) {

        String[] col = new String[rows];
        Column c = this.getColumnCopy(colName);

        for (int i = 0; i < rows; i += 1) {
            col[i] = (String) c.getItem(i);
        }

        return col;
    }

    /**returns a string with column name and type*/
    String getColString() {
        String s = "";
        int i = 0;
        for (; i < cols.length - 1; i += 1) {
            s += cols[i].name + " " + cols[i].type + ",";
        }
        s += cols[i].name + " " + cols[i].type;

        return s;
    }

    /**copies a column, returns a new nondetructive column*/
    Column getColumnCopy(String name) {

        try {
            Column c = cols[indexer.get(name)].copyCol();
            return c;
        } catch (NullPointerException e) {
            return null;
        }

    }

    Column getColumn(String name) {
        try {
            return cols[indexer.get(name)];
        } catch (NullPointerException e) {
            return null;
        }

    }


    public String getType(String name) {
        int i = indexer.get(name);
        return colTypes[i];
    }


    void insertColumn(Column c) {

        cols[indexer.get(c.name)] = c.copyCol();
        this.rows = c.size();

    }

    /**naively inserts a column assuming the rows are the same*/
    void insertNewColumn(Column c) {
        Column[] newCols = new Column[cols.length + 1];
        String[] newNames = new String[cols.length + 1];
        String[] newTypes = new String[cols.length + 1];
        indexer.put(c.name, cols.length);
        this.rows = c.size();
        System.arraycopy(cols, 0, newCols, 0, cols.length);
        System.arraycopy(colNames, 0, newNames, 0, cols.length);
        System.arraycopy(colTypes, 0, newTypes, 0, cols.length);
        newCols[cols.length] = c;
        newNames[cols.length] = c.name;
        newTypes[cols.length] = c.type;
        colNames = newNames;
        colTypes = newTypes;
        cols = newCols;
    }


    /**creates a string to print according to type convention*/
    String stringConverter(Column c, int index) {

        try {
            String val = (String) c.getItem(index - 1);
            if (val.equals(NOVALUE)) {
                return NOVALUE;
            }
            if (val.equals(NAN)) {
                return NAN;
            }
            if (c.type.equals("string")) {
                return val;
            }
            if (c.type.equals("float")) {
                return String.format("%.3f", Float.parseFloat(val));
            } else {
                return val;
            }

        } catch (IndexOutOfBoundsException e) {
            return "ERROR: " + e;
        }


    }

    /**returns a table string in the right format to print*/
    String getTable() {

        String table = "";

        table += this.getColString();

        if (this.rows == 0) {
            return table;
        }
        table += "\n";
        int i = 1;
        for (; i < this.rows; i += 1) {
            table += this.getRowString(i) + "\n";
        }
        table += this.getRowString(i);

        return table;


    }

    /** combines rows from different tables*/
    String[] combineRows(Table t2, int t1index, int t2index) {

        String[] combined = new String[this.cols.length + t2.colSize()];
        String[] t1Rows = getRow(t1index);
        String[] t2Rows = t2.getRow(t2index);

        System.arraycopy(t1Rows, 0, combined, 0, t1Rows.length);
        System.arraycopy(t2Rows, 0, combined, t1Rows.length, t2Rows.length);

        return combined;

    }

    String print() {
        return this.getTable();
    }

    /**handles an arithmetic operation between 2 columns*/
    protected Column arithmaticOperator(String name, String lCol, String rCol, String operator) {
        Column lhs = this.getColumn(lCol);
        Column rhs = this.getColumn(rCol);


        if (rhs == null) {
            String rhsType;
            try {
                Integer.parseInt(rCol);
                rhsType = "int";

            } catch (NumberFormatException e) {
                Float.parseFloat(rCol);
                rhsType = "float";
            }
            Column newColumn = new Column(name, lhs.combinedType(rhsType));
            for (int i = 0; i < lhs.size(); i += 1) {
                newColumn.insertItem(Operation.operatorEval((String) lhs.getItem(i),
                        rCol, (String) lhs.type, rhsType, operator));
            }
            return newColumn;
        } else {
            Column newColumn = new Column(name, lhs.combinedType(rhs.type));
            for (int i = 0; i < lhs.size(); i += 1) {
                newColumn.insertItem(Operation.operatorEval((String) lhs.getItem(i),
                        (String) rhs.getItem(i), (String) lhs.type, (String) rhs.type, operator));
            }
            return newColumn;
        }


    }
    /**handles a comparison operation between 2 columns*/
    protected void comparisonOperation(String lCol, String rCol, String comparator) {

        Column lhs = this.getColumn(lCol);
        Column rhs = this.getColumn(rCol);

        if (lhs == null) {
            throw new ArithmeticException("Malformed select");
        }
        int flag;

        if (rhs == null) {
            String rhsType;
            try {
                Integer.parseInt(rCol);
                rhsType = "int";

            } catch (NumberFormatException e) {
                try {
                    Float.parseFloat(rCol);
                    rhsType = "float";
                } catch (NumberFormatException d) {
                    if (!rCol.contains("'")) {
                        throw new ArithmeticException("NOT STRING");
                    }
                    rhsType = "string";
                }

            }

            for (int i = 0; i < lhs.size(); i += 1) {
                flag = Operation.comparisonEval((String) lhs.getItem(i),
                        rCol, (String) lhs.type, rhsType, comparator);
                if (flag < 0) {
                    this.removeRow(i);
                    i -= 1;
                } else if (flag == 0) {
                    throw new ArithmeticException("Malformed operation");
                }
            }
        } else {

            for (int i = 0; i < lhs.size(); i += 1) {
                String lItem = (String) lhs.getItem(i);
                String rItem = (String) rhs.getItem(i);
                flag = Operation.comparisonEval(lItem, rItem,
                        (String) lhs.type, (String) rhs.type, comparator);
                if (flag < 0) {
                    this.removeRow(i);
                    i -= 1;
                } else if (flag == 0) {
                    throw new ArithmeticException("Malformed operation");
                }

            }
        }
    }
}


    /**
     * creates a column according to the type specified
     */
    class Column<T> {

        String type;
        private String name;
        private ArrayList col;

        Column(String name, String type) {

            this.name = name;

            this.type = type;

            col = new ArrayList<T>();
        }

        /**
         * Adds an item to the end of the ArrayList, increments items, and if needed the row size
         * Takes a generic type T as an argument.
         */
        private void insertItem(String item) {

            if (checkType(item)) {
                col.add(item);
            }


        }

        private void removeItem(int i) {
            col.remove(i);
        }

        private T getItem(int index) {

            try {
                return (T) col.get(index);
            } catch (NullPointerException e) {
                throw (e);
            } catch (IndexOutOfBoundsException e) {
                throw (e);
            }

        }

        private int size() {
            return col.size();
        }


        private Column copyCol() {
            Column newCol = new Column(this.name, this.type);
            for (int i = 0; i < this.size(); i += 1) {
                newCol.insertItem((String) this.getItem(i));
            }

            return newCol;
        }

        /**make sure that the type inserted to the column is correct*/
        boolean checkType(String item) {


            if (item.equals(NOVALUE)) {
                return true;
            }

            if (item.equals(NAN)) {
                return true;
            }

            if (this.type.equals("float")) {
                float temp = 0;
                try {
                    temp = Float.parseFloat(item);
                    return true;
                } catch (NumberFormatException e) {
                    throw (e);
                }
            } else if (type.equals("int")) {
                int temp = 0;
                try {
                    temp = Integer.parseInt(item);
                    return true;
                } catch (NumberFormatException e) {
                    throw (e);
                }
            } else if (this.type.equals("string")) {
                try {
                    if (item.contains("'")) {
                        return true;
                    } else {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException e) {
                    throw(e);
                }

            }


            return false;

        }

        /**decides what is the combined type*/
        String combinedType(String rhs) {
            if (this.type.equals("float") || rhs.equals("float")) {
                return "float";
            } else if (this.type.equals("int") && rhs.equals("int")) {
                return "int";
            } else {
                return "string";
            }
        }
    }


}

