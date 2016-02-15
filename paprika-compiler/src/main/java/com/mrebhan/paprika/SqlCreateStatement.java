package com.mrebhan.paprika;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;

import static com.mrebhan.paprika.Column.FLAG_PRIMARY_KEY;

public final class SqlCreateStatement {

    private List<Column> columns;
    private String tableName;

    public SqlCreateStatement(Map<String, Element> elementMap, Element parent) {
        this.tableName = parent.getSimpleName().toString();

        columns = new ArrayList<>();

        boolean primaryKeyUsed = false;

        for (String key : elementMap.keySet()) {
            Column column = new Column(elementMap.get(key));

            if ((column.flags & FLAG_PRIMARY_KEY) != 0) {
                if (!primaryKeyUsed) {
                    primaryKeyUsed = true;
                } else {
                    throw new IllegalArgumentException("You can only specify one primary key! table = " + tableName);
                }
            }

            columns.add(column);
        }

        if (!primaryKeyUsed) {
            throw new IllegalArgumentException("Please specify a primary key! table = " + tableName);
        }
    }

    @Override
    public String toString() {

        StringBuilder statement =  new StringBuilder("CREATE TABLE " + tableName + "( ");

        for (Column column : columns) {
            statement.append(column.toString());
            statement.append(", ");
        }

        statement.replace(statement.length() - 2, statement.length(), ")");

        return statement.toString();
    }
}
