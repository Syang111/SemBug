package Sonar.postgres.gen;

import Sonar.IgnoreMeException;
import Sonar.Randomly;
import Sonar.common.query.SQLQueryAdapter;
import Sonar.postgres.PostgresGlobalState;
import Sonar.postgres.PostgresSchema.PostgresTable;


public final class PostgresCommentGenerator {

    private PostgresCommentGenerator() {
    }

    private enum Action {
        INDEX, COLUMN, STATISTICS, TABLE
    }

    public static SQLQueryAdapter generate(PostgresGlobalState globalState) {
        StringBuilder sb = new StringBuilder();
        sb.append("COMMENT ON ");
        Action type = Randomly.fromOptions(Action.values());
        PostgresTable randomTable = globalState.getSchema().getRandomTable();
        switch (type) {
        case INDEX:
            sb.append("INDEX ");
            if (randomTable.getIndexes().isEmpty()) {
                throw new IgnoreMeException();
            } else {
                sb.append(randomTable.getRandomIndex().getIndexName());
            }
            break;
        case COLUMN:
            sb.append("COLUMN ");
            sb.append(randomTable.getRandomColumn().getFullQualifiedName());
            break;
        case STATISTICS:
            sb.append("STATISTICS ");
            if (randomTable.getStatistics().isEmpty()) {
                throw new IgnoreMeException();
            } else {
                sb.append(randomTable.getStatistics().get(0).getName());
            }
            break;
        case TABLE:
            sb.append("TABLE ");
            if (randomTable.isView()) {
                throw new IgnoreMeException();
            }
            sb.append(randomTable.getName());
            break;
        default:
            throw new AssertionError(type);
        }
        sb.append(" IS ");
        if (Randomly.getBoolean()) {
            sb.append("NULL");
        } else {
            sb.append("'");
            sb.append(globalState.getRandomly().getString().replace("'", "''"));
            sb.append("'");
        }
        return new SQLQueryAdapter(sb.toString());
    }

}
