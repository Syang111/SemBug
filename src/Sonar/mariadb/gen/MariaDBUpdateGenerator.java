package Sonar.mariadb.gen;

import Sonar.Randomly;
import Sonar.common.query.ExpectedErrors;
import Sonar.common.query.SQLQueryAdapter;
import Sonar.mariadb.MariaDBErrors;
import Sonar.mariadb.MariaDBSchema;
import Sonar.mariadb.MariaDBSchema.MariaDBTable;
import Sonar.mariadb.ast.MariaDBVisitor;

public final class MariaDBUpdateGenerator {

    private MariaDBUpdateGenerator() {
    }

    public static SQLQueryAdapter update(MariaDBSchema s, Randomly r) {
        MariaDBTable randomTable = s.getRandomTable();
        StringBuilder sb = new StringBuilder("UPDATE ");
        if (Randomly.getBoolean()) {
            sb.append("LOW_PRIORITY ");
        }
        if (Randomly.getBoolean()) {
            sb.append("IGNORE ");
        }
        sb.append(randomTable.getName());
        sb.append(" SET ");
        for (int i = 0; i < Randomly.smallNumber() + 1; i++) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append(randomTable.getRandomColumn().getName());
            sb.append("=");
            if (Randomly.getBoolean()) {
                sb.append(MariaDBVisitor.asString(MariaDBExpressionGenerator.getRandomConstant(r)));
            } else {
                sb.append("DEFAULT");
            }

        }
        ExpectedErrors errors = new ExpectedErrors();
        MariaDBErrors.addInsertErrors(errors);
        return new SQLQueryAdapter(sb.toString(), errors);
    }

}
