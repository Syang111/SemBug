package Sonar.mysql.gen;

import Sonar.IgnoreMeException;
import Sonar.Randomly;
import Sonar.common.query.ExpectedErrors;
import Sonar.common.query.SQLQueryAdapter;
import Sonar.mysql.MySQLGlobalState;
import Sonar.mysql.MySQLSchema.MySQLTable;


public final class MySQLDropIndex {

    private MySQLDropIndex() {
    }










    public static SQLQueryAdapter generate(MySQLGlobalState globalState) {
        MySQLTable table = globalState.getSchema().getRandomTable();
        if (!table.hasIndexes()) {
            throw new IgnoreMeException();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("DROP INDEX ");
        sb.append(table.getRandomIndex().getIndexName());
        sb.append(" ON ");
        sb.append(table.getName());
        if (Randomly.getBoolean()) {
            sb.append(" ALGORITHM=");
            sb.append(Randomly.fromOptions("DEFAULT", "INPLACE", "COPY"));
        }
        if (Randomly.getBoolean()) {
            sb.append(" LOCK=");
            sb.append(Randomly.fromOptions("DEFAULT", "NONE", "SHARED", "EXCLUSIVE"));
        }
        return new SQLQueryAdapter(sb.toString(),
                ExpectedErrors.from("LOCK=NONE is not supported", "ALGORITHM=INPLACE is not supported",
                        "Data truncation", "Data truncated for functional index",
                        "A primary key index cannot be invisible"));
    }

}
