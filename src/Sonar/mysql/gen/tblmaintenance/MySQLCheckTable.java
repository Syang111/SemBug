package Sonar.mysql.gen.tblmaintenance;

import java.util.List;
import java.util.stream.Collectors;

import Sonar.Randomly;
import Sonar.common.query.SQLQueryAdapter;
import Sonar.mysql.MySQLGlobalState;
import Sonar.mysql.MySQLSchema.MySQLTable;


public class MySQLCheckTable {

    private final List<MySQLTable> tables;
    private final StringBuilder sb = new StringBuilder();

    public MySQLCheckTable(List<MySQLTable> tables) {
        this.tables = tables;
    }

    public static SQLQueryAdapter check(MySQLGlobalState globalState) {
        return new MySQLCheckTable(globalState.getSchema().getDatabaseTablesRandomSubsetNotEmpty()).generate();
    }











    private SQLQueryAdapter generate() {
        sb.append("CHECK TABLE ");
        sb.append(tables.stream().map(t -> t.getName()).collect(Collectors.joining(", ")));
        sb.append(" ");
        List<String> options = Randomly.subset("FOR UPGRADE", "QUICK", "FAST", "MEDIUM", "EXTENDED", "CHANGED");
        sb.append(options.stream().collect(Collectors.joining(" ")));
        return new SQLQueryAdapter(sb.toString());
    }

}
