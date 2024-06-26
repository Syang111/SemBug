package Sonar.mariadb.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Sonar.Randomly;
import Sonar.mysql.ast.MySQLCastOperation;
import Sonar.mysql.ast.MySQLConstant;
import Sonar.mysql.ast.MySQLExpression;

public enum MariaDBFunctionName {




    CONCAT("CONCAT", 2, FunctionAttribute.VARIADIC),


    IF("IF", 3),
    IFNULL("IFNULL", 2),
    BITCOUNT("BIT_COUNT", 1),
    LENGTH("LENGTH", 1), LENGTHB("LENGTHB", 1), LOCATE1("LOCATE", 2), LOCATE2("LOCATE", 3),

    ORD("ORD", 1),


    TRIM("TRIM", 1),
    UNCOMPRESSED_LENGTH("UNCOMPRESSED_LENGTH", 1),
    ABS("ABS",1),






















    COALESCE("COALESCE",2);

    String functionName;
    private List<FunctionAttribute> functionAttributes;
    private int nrArgs;

    MariaDBFunctionName(String functionName, int nrArgs, FunctionAttribute... functionAttributes) {
        this.functionName = functionName;
        this.nrArgs = nrArgs;
        this.functionAttributes = new ArrayList<>(Arrays.asList(functionAttributes));
    }

    enum FunctionAttribute {
        VARIADIC
    }

    ;

    public String getFunctionName() {
        return functionName;
    }

    public int getNrArgs() {
        return nrArgs;
    }

    public List<FunctionAttribute> getFunctionAttributes() {
        return functionAttributes;
    }

    public boolean isVariadic() {
        return functionAttributes.stream().anyMatch(p -> p == FunctionAttribute.VARIADIC);
    }

    public static MariaDBFunctionName getRandom() {
        return Randomly.fromOptions(MariaDBFunctionName.values());
    }
}