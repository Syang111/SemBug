package Sonar.sqlite3.oracle;

import java.util.ArrayList;
import java.util.List;

import Sonar.Randomly;
import Sonar.sqlite3.SQLite3GlobalState;
import Sonar.sqlite3.ast.SQLite3Constant;
import Sonar.sqlite3.ast.SQLite3Expression;
import Sonar.sqlite3.ast.SQLite3Select;
import Sonar.sqlite3.ast.SQLite3Select.SelectType;
import Sonar.sqlite3.ast.SQLite3SetClause;
import Sonar.sqlite3.ast.SQLite3SetClause.SQLite3ClauseType;
import Sonar.sqlite3.ast.SQLite3WindowFunction;
import Sonar.sqlite3.ast.SQLite3WindowFunctionExpression;
import Sonar.sqlite3.ast.SQLite3WindowFunctionExpression.SQLite3FrameSpecExclude;
import Sonar.sqlite3.ast.SQLite3WindowFunctionExpression.SQLite3FrameSpecKind;
import Sonar.sqlite3.ast.SQLite3WindowFunctionExpression.SQLite3WindowFunctionFrameSpecBetween;
import Sonar.sqlite3.ast.SQLite3WindowFunctionExpression.SQLite3WindowFunctionFrameSpecTerm;
import Sonar.sqlite3.ast.SQLite3WindowFunctionExpression.SQLite3WindowFunctionFrameSpecTerm.SQLite3WindowFunctionFrameSpecTermKind;
import Sonar.sqlite3.gen.SQLite3Common;
import Sonar.sqlite3.gen.SQLite3ExpressionGenerator;
import Sonar.sqlite3.schema.SQLite3Schema;
import Sonar.sqlite3.schema.SQLite3Schema.SQLite3Table;
import Sonar.sqlite3.schema.SQLite3Schema.SQLite3Tables;

public final class SQLite3RandomQuerySynthesizer {

    private SQLite3RandomQuerySynthesizer() {
    }



    public static SQLite3Expression generate(SQLite3GlobalState globalState, int size) {
        Randomly r = globalState.getRandomly();
        SQLite3Schema s = globalState.getSchema();
        SQLite3Tables targetTables = s.getRandomTableNonEmptyTables();
        List<SQLite3Expression> expressions = new ArrayList<>();
        SQLite3ExpressionGenerator gen = new SQLite3ExpressionGenerator(globalState)
                .setColumns(s.getTables().getColumns());
        SQLite3ExpressionGenerator whereClauseGen = new SQLite3ExpressionGenerator(globalState);
        SQLite3ExpressionGenerator aggregateGen = new SQLite3ExpressionGenerator(globalState)
                .setColumns(s.getTables().getColumns()).allowAggregateFunctions();


        SQLite3Select select = new SQLite3Select();

        select.setSelectType(Randomly.fromOptions(SelectType.values()));
        for (int i = 0; i < size; i++) {
            if (Randomly.getBooleanWithRatherLowProbability()) {
                SQLite3Expression baseWindowFunction;
                boolean normalAggregateFunction = Randomly.getBoolean();
                if (!normalAggregateFunction) {
                    baseWindowFunction = SQLite3WindowFunction.getRandom(targetTables.getColumns(), globalState);
                } else {
                    baseWindowFunction = gen.getAggregateFunction(true);
                    assert baseWindowFunction != null;
                }
                SQLite3WindowFunctionExpression windowFunction = new SQLite3WindowFunctionExpression(
                        baseWindowFunction);
                if (Randomly.getBooleanWithRatherLowProbability() && normalAggregateFunction) {
                    windowFunction.setFilterClause(gen.generateExpression());
                }
                if (Randomly.getBooleanWithRatherLowProbability()) {
                    windowFunction.setOrderBy(gen.generateOrderBys());
                }
                if (Randomly.getBooleanWithRatherLowProbability()) {
                    windowFunction.setPartitionBy(gen.getRandomExpressions(Randomly.smallNumber()));
                }
                if (Randomly.getBooleanWithRatherLowProbability()) {
                    windowFunction.setFrameSpecKind(SQLite3FrameSpecKind.getRandom());
                    SQLite3Expression windowFunctionTerm;
                    if (Randomly.getBoolean()) {
                        windowFunctionTerm = new SQLite3WindowFunctionFrameSpecTerm(
                                Randomly.fromOptions(SQLite3WindowFunctionFrameSpecTermKind.UNBOUNDED_PRECEDING,
                                        SQLite3WindowFunctionFrameSpecTermKind.CURRENT_ROW));
                    } else if (Randomly.getBoolean()) {
                        windowFunctionTerm = new SQLite3WindowFunctionFrameSpecTerm(gen.generateExpression(),
                                SQLite3WindowFunctionFrameSpecTermKind.EXPR_PRECEDING);
                    } else {
                        SQLite3WindowFunctionFrameSpecTerm left = getTerm(true, gen);
                        SQLite3WindowFunctionFrameSpecTerm right = getTerm(false, gen);
                        windowFunctionTerm = new SQLite3WindowFunctionFrameSpecBetween(left, right);
                    }
                    windowFunction.setFrameSpec(windowFunctionTerm);
                    if (Randomly.getBoolean()) {
                        windowFunction.setExclude(SQLite3FrameSpecExclude.getRandom());
                    }
                }
                expressions.add(windowFunction);
            } else {
                expressions.add(aggregateGen.generateExpression());
            }
        }
        select.setFetchColumns(expressions);
        List<SQLite3Table> tables = targetTables.getTables();
        if (Randomly.getBooleanWithRatherLowProbability()) {

            select.setJoinClauses(gen.getRandomJoinClauses(tables));
        }

        select.setFromList(SQLite3Common.getTableRefs(tables, s));






        if (Randomly.getBoolean()) {
            select.setWhereClause(whereClauseGen.generateExpression());
        }
        boolean groupBy = Randomly.getBooleanWithRatherLowProbability();
        if (groupBy) {

            select.setGroupByClause(gen.getRandomExpressions(Randomly.smallNumber() + 1));
            if (Randomly.getBoolean()) {

                select.setHavingClause(aggregateGen.generateExpression());
            }
        }
        boolean orderBy = Randomly.getBooleanWithRatherLowProbability();
        if (orderBy) {

            select.setOrderByExpressions(gen.generateOrderBys());
        }
        if (Randomly.getBooleanWithRatherLowProbability()) {

            select.setLimitClause(SQLite3Constant.createIntConstant(r.getInteger()));
            if (Randomly.getBoolean()) {

                select.setOffsetClause(SQLite3Constant.createIntConstant(r.getInteger()));
            }
        }
        if (!orderBy && !groupBy && Randomly.getBooleanWithSmallProbability()) {
            return new SQLite3SetClause(select, generate(globalState, size), SQLite3ClauseType.getRandom());
        }
        return select;
    }

    private static SQLite3WindowFunctionFrameSpecTerm getTerm(boolean isLeftTerm, SQLite3ExpressionGenerator gen) {
        if (Randomly.getBoolean()) {
            SQLite3Expression expr = gen.generateExpression();
            SQLite3WindowFunctionFrameSpecTermKind kind = Randomly.fromOptions(
                    SQLite3WindowFunctionFrameSpecTermKind.EXPR_FOLLOWING,
                    SQLite3WindowFunctionFrameSpecTermKind.EXPR_PRECEDING);
            return new SQLite3WindowFunctionFrameSpecTerm(expr, kind);
        } else if (Randomly.getBoolean()) {
            return new SQLite3WindowFunctionFrameSpecTerm(SQLite3WindowFunctionFrameSpecTermKind.CURRENT_ROW);
        } else {
            if (isLeftTerm) {
                return new SQLite3WindowFunctionFrameSpecTerm(
                        SQLite3WindowFunctionFrameSpecTermKind.UNBOUNDED_PRECEDING);
            } else {
                return new SQLite3WindowFunctionFrameSpecTerm(
                        SQLite3WindowFunctionFrameSpecTermKind.UNBOUNDED_FOLLOWING);
            }
        }
    }

}
