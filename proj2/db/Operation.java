package db;

import java.util.LinkedList;

/**
 * Variables:
 * Methods:
 * eval
 * Constrictions:
 * Notes: Create table according to conditions
 * This Class handles operational conditions and comparison conditions
 */
class Operation {
    static final String NOVALUE = "NOVALUE";
    static final String NAN = "NaN";
    private Database db;

    //    Operation Constructor
    Operation(Database d) {
        db = d;
    }

    static String operatorEval(String lhs, String rhs,
                               String lhsType, String rhsType, String operator) {

        try {
            if (lhs.equals(NAN) || lhs.equals(NOVALUE) || rhs.equals(NAN) || rhs.equals(NOVALUE)) {
                return operationSpecial(lhs, rhs, lhsType, rhsType, operator);
            }
            if (lhsType.equals("int") && rhsType.equals("int")) {
                return operations(Integer.parseInt(lhs), Integer.parseInt(rhs), operator);
            } else if (lhsType.equals("int") && rhsType.equals("float")) {
                return operations(Integer.parseInt(lhs), Float.parseFloat(rhs), operator);
            } else if (lhsType.equals("float") && rhsType.equals("int")) {
                return operations(Float.parseFloat(lhs), Integer.parseInt(rhs), operator);
            } else if (lhsType.equals("float") && rhsType.equals("float")) {
                return operations(Float.parseFloat(lhs), Float.parseFloat(rhs), operator);
            } else if ((lhsType.equals("String") | lhsType.equals("string"))
                    && (rhsType.equals("string") | rhsType.equals("String"))) {
                return stringAddition(lhs, rhs, operator);
            } else {
                throw new ArithmeticException("ERROR: Malformed operation");
            }
        } catch (ArithmeticException e) {
            throw new ArithmeticException();
        }


    }

    static String stringAddition(String lhs, String rhs, String operator) {
        String[] s1 = lhs.split("'");
        String[] s2 = rhs.split("'");
        String result = "\'" + s1[1] + s2[1] + "\'";

        return result;
    }

    static String operations(int lhs, int rhs, String operator) {

        String result = "";
        switch (operator) {
            case "+":
                result = Integer.toString(lhs + rhs);
                break;
            case "-":
                result = Integer.toString(lhs - rhs);
                break;
            case "*":
                result = Integer.toString(lhs * rhs);
                break;
            case "/": {
                try {
                    result = Float.isInfinite(lhs / rhs) || Float.isNaN(lhs / rhs)
                            ? "NaN" : Integer.toString(lhs / rhs);
                } catch (ArithmeticException e) {
                    return "NaN";
                }

            }
            break;
            default: break;
        }
        return result;

    }

    static String operations(float lhs, int rhs, String operator) {

        String result = "";
        switch (operator) {
            case "+":
                result = Float.toString(lhs + rhs);
                break;
            case "-":
                result = Float.toString(lhs - rhs);
                break;
            case "*":
                result = Float.toString(lhs * rhs);
                break;
            case "/":
                result = Float.isInfinite(lhs / rhs) || Float.isNaN(lhs / rhs)
                        ? "NaN" : Float.toString(lhs / rhs);
                break;
            default: break;

        }

        return result;

    }

    static String operations(int lhs, float rhs, String operator) {

        String result = "";
        switch (operator) {
            case "+":
                result = Float.toString(lhs + rhs);
                break;
            case "-":
                result = Float.toString(lhs - rhs);
                break;
            case "*":
                result = Float.toString(lhs * rhs);
                break;
            case "/":
                result = Float.isInfinite(lhs / rhs) || Float.isNaN(lhs / rhs)
                        ? "NaN" : Float.toString(lhs / rhs);
                break;
            default: break;

        }

        return result;

    }

    static String operations(float lhs, float rhs, String operator) {

        String result = "";
        switch (operator) {
            case "+":
                result = Float.toString(lhs + rhs);
                break;
            case "-":
                result = Float.toString(lhs - rhs);
                break;
            case "*":
                result = Float.toString(lhs * rhs);
                break;
            case "/":
                result = Float.isInfinite(lhs / rhs) || Float.isNaN(lhs / rhs)
                        ? "NaN" : Float.toString(lhs / rhs);
                break;
            default: break;

        }
        return result;
    }

    static String operationSpecial(String lhs, String rhs,
                                   String lhsType, String rhsType, String comparison) {

        if (lhs.equals(NOVALUE)) {
            return rhs;
        } else if (rhs.equals(NOVALUE)) {
            return lhs;
        } else {
            return NAN;
        }
    }

    //==, !=, <, >, <= and >=
    static int comparisonEval(String lhs, String rhs, String lhsType,
                              String rhsType, String comperator) {

        try {

            if (lhs.equals(NAN) || lhs.equals(NOVALUE) || rhs.equals(NAN) || rhs.equals(NOVALUE)) {
                return comparisonSpecial(lhs, rhs, lhsType, rhsType, comperator);
            }
            if (lhsType.equals("int") && rhsType.equals("int")) {
                return comparison(Integer.parseInt(lhs), Integer.parseInt(rhs), comperator);
            } else if (lhsType.equals("int") && rhsType.equals("float")) {
                return comparison(Integer.parseInt(lhs), Float.parseFloat(rhs), comperator);
            } else if (lhsType.equals("float") && rhsType.equals("int")) {
                return comparison(Float.parseFloat(lhs), Integer.parseInt(rhs), comperator);
            } else if (lhsType.equals("float") && rhsType.equals("float")) {
                return comparison(Float.parseFloat(lhs), Float.parseFloat(rhs), comperator);
            } else if ((lhsType.equals("String") | lhsType.equals("string"))
                    && (rhsType.equals("string") | rhsType.equals("String"))) {
                return stringComparison(lhs, rhs, comperator);
            } else {
                throw new ArithmeticException("ERROR: Malformed operation");
            }
        } catch (ArithmeticException e) {
            return 0;
        }


    }

    static int stringComparison(String lhs, String rhs, String comparator) {
        int result = 0;
        switch (comparator) {
            case ">":
                result = lhs.compareTo(rhs) > 0 ? 1 : -1;
                break;
            case "<":
                result = (lhs.compareTo(rhs) < 0 ? 1 : -1);
                break;
            case "==":
                result = (lhs.compareTo(rhs) == 0 ? 1 : -1);
                break;
            case ">=":
                result = (lhs.compareTo(rhs) >= 0 ? 1 : -1);
                break;
            case "<=":
                result = (lhs.compareTo(rhs) <= 0 ? 1 : -1);
                break;
            case "!=":
                result = (lhs.compareTo(rhs) == 0 ? -1 : 1);
                break;
            default: break;

        }
        return result;
    }

    static int comparison(int lhs, int rhs, String comparison) {
        int result = 0;
        switch (comparison) {
            case ">":
                result = lhs > rhs ? 1 : -1;
                break;
            case "<":
                result = lhs < rhs ? 1 : -1;
                break;
            case "==":
                result = lhs == rhs ? 1 : -1;
                break;
            case ">=":
                result = lhs >= rhs ? 1 : -1;
                break;
            case "<=":
                result = lhs <= rhs ? 1 : -1;
                break;
            case "!=":
                result = lhs != rhs ? 1 : -1;
                break;
            default: break;

        }
        return result;

    }

    static int comparison(int lhs, float rhs, String comparison) {
        int result = 0;
        switch (comparison) {
            case ">":
                result = lhs > rhs ? 1 : -1;
                break;
            case "<":
                result = lhs < rhs ? 1 : -1;
                break;
            case "==":
                result = lhs == rhs ? 1 : -1;
                break;
            case ">=":
                result = lhs >= rhs ? 1 : -1;
                break;
            case "<=":
                result = lhs <= rhs ? 1 : -1;
                break;
            case "!=":
                result = lhs != rhs ? 1 : -1;
                break;
            default: break;

        }
        return result;

    }

    static int comparison(float lhs, int rhs, String comparison) {
        int result = 0;
        switch (comparison) {
            case ">":
                result = lhs > rhs ? 1 : -1;
                break;
            case "<":
                result = lhs < rhs ? 1 : -1;
                break;
            case "==":
                result = lhs == rhs ? 1 : -1;
                break;
            case ">=":
                result = lhs >= rhs ? 1 : -1;
                break;
            case "<=":
                result = lhs <= rhs ? 1 : -1;
                break;
            case "!=":
                result = lhs != rhs ? 1 : -1;
                break;
            default: break;

        }
        return result;

    }

    static int comparison(float lhs, float rhs, String comparison) {
        int result = 0;
        switch (comparison) {
            case ">":
                result = lhs > rhs ? 1 : -1;
                break;
            case "<":
                result = lhs < rhs ? 1 : -1;
                break;
            case "==":
                result = lhs == rhs ? 1 : -1;
                break;
            case ">=":
                result = lhs >= rhs ? 1 : -1;
                break;
            case "<=":
                result = lhs <= rhs ? 1 : -1;
                break;
            case "!=":
                result = lhs != rhs ? 1 : -1;
                break;
            default: break;

        }
        return result;

    }

    static int comparisonSpecial(String lhs, String rhs,
                                 String lhsType, String rhsType, String comparison) {
        int result = 0;
        if (lhs.equals(NAN)) {
            lhs = rhs + 1;
            return comparisonEval(lhs, rhs, lhsType, rhsType, comparison);
        } else if (lhs.equals(NOVALUE) || rhs.equals(NOVALUE)) {
            return -1;
        } else {
            rhs = lhs + 1;
            return comparisonEval(lhs, rhs, lhsType, rhsType, comparison);
        }
    }

    String eval(LinkedList<String> query) {

        return "";
    }

}
