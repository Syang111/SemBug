package Sonar.mysql.gen.tblmaintenance;

import java.util.List;
import java.util.stream.Collectors;

import Sonar.Randomly;
import Sonar.common.query.SQLQueryAdapter;
import Sonar.mysql.MySQLGlobalState;
import Sonar.mysql.MySQLSchema.MySQLTable;


public class MySQLOptimize {

    private final List<MySQLTable> tables;
    private final StringBuilder sb = new StringBuilder();

    public MySQLOptimize(List<MySQLTable> tables) {
        this.tables = tables;
    }

    public static SQLQueryAdapter optimize(MySQLGlobalState globalState) {
        return new MySQLOptimize(globalState.getSchema().getDatabaseTablesRandomSubsetNotEmpty()).optimize();
    }



    private SQLQueryAdapter optimize() {
        sb.append("OPTIMIZE");
        if (Randomly.getBoolean()) {
            sb.append(" ");
            sb.append(Randomly.fromOptions("NO_WRITE_TO_BINLOG", "LOCAL"));
        }
        sb.append(" TABLE ");
        sb.append(tables.stream().map(t -> t.getName()).collect(Collectors.joining(", ")));
        return new SQLQueryAdapter(sb.toString());
    }

}
