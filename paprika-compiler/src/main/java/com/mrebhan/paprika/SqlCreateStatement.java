package com.mrebhan.paprika;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;

public final class SqlCreateStatement {

    private List<Column> columns;
    private String tableName;

    public SqlCreateStatement(Map<String, Element> elementMap, Element parent) {
        this.tableName = parent.getSimpleName().toString();

        columns = new ArrayList<>();

        // TODO move this
        columns.add(Column.createID());

        for (String key : elementMap.keySet()) {
            columns.add(new Column(elementMap.get(key)));
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
