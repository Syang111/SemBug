package Sonar.sqlite3.gen.ddl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Sonar.IgnoreMeException;
import Sonar.Randomly;
import Sonar.common.DBMSCommon;
import Sonar.common.query.ExpectedErrors;
import Sonar.common.query.SQLQueryAdapter;
import Sonar.sqlite3.SQLite3GlobalState;

public class SQLite3CreateVirtualFTSTableGenerator {

    private final String tableName;
    private final Randomly r;
    private final StringBuilder sb = new StringBuilder();

    private final SQLite3GlobalState globalState;

    public SQLite3CreateVirtualFTSTableGenerator(String tableName, Randomly r,SQLite3GlobalState globalState) {
        this.tableName = tableName;
        this.r = r;
        this.globalState = globalState;
    }

    public static SQLQueryAdapter createRandomTableStatement(SQLite3GlobalState globalState) {
        if (globalState.getSchema().getTables().getTables()
                .size() > globalState.getDbmsSpecificOptions().maxNumTables) {
            throw new IgnoreMeException();
        }
        return createTableStatement(globalState.getSchema().getFreeVirtualTableName(),globalState.getRandomly(),globalState);
    }

    public static SQLQueryAdapter createTableStatement(String tableName, Randomly r,SQLite3GlobalState globalState) {
        return new SQLite3CreateVirtualFTSTableGenerator(tableName,r,globalState).create();
    }

    private enum Fts5Options {
        PREFIX,
        TOKENIZE,
        COLUMNSIZE,
        DETAIL,
        CONTENTLESS,
    };

    private enum Fts4Options {
        MATCHINFO, TOKENIZE, PREFIX, ORDER, LANGUAGEID, COMPRESS, NOT_INDEXED
    }

    public SQLQueryAdapter create() {
        sb.append("CREATE VIRTUAL TABLE ");
        sb.append(tableName);
        sb.append(" USING ");
        if (Randomly.getBoolean()) {
            createFts4Table();
        } else {
            createFts5Table();
        }
        return new SQLQueryAdapter(sb.toString(),
                ExpectedErrors.from("unrecognized parameter", "unknown tokenizer: ascii"), true);
    }

    private void createFts4Table() {
        createTable("fts4", () -> {
            if (Randomly.getBoolean()) {
                sb.append(" UNINDEXED");
            }
        }, (min_colNumber,max_colNumber) -> {
            List<Fts4Options> possibleActions = new ArrayList<>(Arrays.asList(Fts4Options.values()));
            for (int i = 0; i < Randomly.smallNumber(); i++) {
                sb.append(", ");
                Fts4Options option = Randomly.fromList(possibleActions);
                switch (option) {
                    case COMPRESS:
                        sb.append("compress=likely, uncompress=likely");
                        break;
                    case MATCHINFO:
                        sb.append("matchinfo=fts3");
                        break;
                    case TOKENIZE:
                        sb.append("tokenize=");
                        sb.append(Randomly.fromOptions("simple", "porter", "unicode61"));
                        possibleActions.remove(Fts4Options.TOKENIZE);
                        break;
                    case PREFIX:
                        sb.append("prefix=");
                        sb.append(r.getInteger(1, 999));
                        sb.append("");
                        break;
                    case ORDER:
                        sb.append("order=");
                        sb.append(Randomly.fromOptions("ASC", "DESC"));
                        break;
                    case LANGUAGEID:
                        sb.append("languageid=\"lid\"");
                        break;
                    case NOT_INDEXED:

                        sb.append("notindexed=c" + r.getInteger(min_colNumber,max_colNumber+1));
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        });
    }

    private void prefix() {
        sb.append("prefix = ");
        sb.append(r.getInteger(1, 999));
    }

    private void createFts5Table() throws AssertionError {
        createTable("fts5", () -> {
            if (Randomly.getBoolean()) {

                sb.append(" UNINDEXED");
            }
        }, (min_colNumber,max_colNumber) -> {
            List<Fts5Options> possibleActions = new ArrayList<>(Arrays.asList(Fts5Options.values()));
            for (int i = 0; i < Randomly.smallNumber(); i++) {
                Fts5Options option = Randomly.fromList(possibleActions);
                sb.append(", ");
                switch (option) {
                    case COLUMNSIZE:
                        if (Randomly.getBoolean()) {
                            sb.append(", columnsize=");
                            sb.append(Randomly.fromOptions(0, 1));
                        }
                        break;
                    case DETAIL:
                        sb.append("detail=");
                        sb.append(Randomly.fromOptions("full", "column", "none"));
                        break;
                    case PREFIX:
                        prefix();
                        break;
                    case TOKENIZE:
                        sb.append("tokenize = ");
                        sb.append("\"");
                        String tokenizer = Randomly.fromOptions("porter ascii", "porter", "unicode61", "ascii");
                        sb.append(tokenizer);
                        if (tokenizer.contentEquals("unicode61")) {
                            if (Randomly.getBoolean()) {
                                sb.append(" remove_diacritics ");
                                sb.append(Randomly.fromOptions(0, 1, 2));
                            }
                            if (Randomly.getBoolean()) {
                                sb.append(" tokenchars ");
                                getString();
                            }
                            if (Randomly.getBoolean()) {
                                sb.append(" separators ");
                                getString();
                            }
                        }
                        sb.append("\"");
                        possibleActions.remove(Fts5Options.TOKENIZE);
                        break;
                    case CONTENTLESS:
                        sb.append("content=''");
                        possibleActions.remove(Fts5Options.CONTENTLESS);
                        break;
                    default:
                        throw new AssertionError(option);
                }
            }
        });
    }

    private void getString() {
        sb.append("'");
        sb.append(r.getString().replace("'", "''").replace("\"", "\"\""));
        sb.append("'");
    }

    public interface Acc {
        void action();
    }

    public interface Act {
        void action(int min_colNumber,int max_colNumber);
    }

    private void createTable(String ftsVersion, Acc columnAction, Act tableAction) {
        sb.append(ftsVersion);
        sb.append("(");
        int min_colNumber=0,max_colNumber=0;
        for (int i = 0; i < 1 + Randomly.smallNumber(); i++) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append(DBMSCommon.createColumnName(globalState.getColumns()));
            if ( i == 0 ) {
                min_colNumber = globalState.getColumns();
            }
            globalState.addColumns(1);
            columnAction.action();
        }
        max_colNumber = globalState.getColumns()-1;

        tableAction.action(min_colNumber, max_colNumber);
        sb.append(")");
    }

}
