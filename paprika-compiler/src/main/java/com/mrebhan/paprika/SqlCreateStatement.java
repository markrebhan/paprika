package com.mrebhan.paprika;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;

import static com.mrebhan.paprika.ColumnDefinition.FLAG_PRIMARY_KEY;

public final class SqlCreateStatement {

    private List<ColumnDefinition> columnDefinitions;
    private String tableName;

    public SqlCreateStatement(Map<String, Element> elementMap, Element parent, SqlUpgradeScripts upgradeScripts) {
        this.tableName = parent.getSimpleName().toString();

        columnDefinitions = new ArrayList<>();

        boolean primaryKeyUsed = false;

        for (String key : elementMap.keySet()) {
            Element element = elementMap.get(key);
            ColumnDefinition columnDefinition = new ColumnDefinition(element);

            Column column = element.getAnnotation(Column.class);

            if (column != null && upgradeScripts != null) {
                int version = column.version();
                if (version > 1) {
                    upgradeScripts.addAlterAddColumn(columnDefinition, tableName, version);
                }
            }

            if ((columnDefinition.flags & FLAG_PRIMARY_KEY) != 0) {
                if (!primaryKeyUsed) {
                    primaryKeyUsed = true;
                } else {
                    throw new IllegalArgumentException("You can only specify one primary key! table = " + tableName);
                }
            }

            columnDefinitions.add(columnDefinition);
        }

//        if (!primaryKeyUsed) {
//            throw new IllegalArgumentException("Please specify a primary key! table = " + tableName);
//        }
    }

    @Override
    public String toString() {

        StringBuilder statement =  new StringBuilder("CREATE TABLE " + tableName + "( ");

        for (ColumnDefinition columnDefinition : columnDefinitions) {
            statement.append(columnDefinition.toString());
            statement.append(", ");
        }

        statement.replace(statement.length() - 2, statement.length(), ")");

        return statement.toString();
    }
}
