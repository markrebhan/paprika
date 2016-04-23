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
        columnDefinitions.add(ColumnDefinition.createPrimaryIdDefinition());

        for (String key : elementMap.keySet()) {
            Element element = elementMap.get(key);
            ColumnDefinition columnDefinition = new ColumnDefinition(element);

            Column column = element.getAnnotation(Column.class);
            ForeignObject foreignObject = element.getAnnotation(ForeignObject.class);

            if (column != null && upgradeScripts != null) {
                int version = column.version();
                if (version > 1) {
                    upgradeScripts.addAlterAddColumn(columnDefinition, tableName, version);
                }
            } else if (foreignObject != null && upgradeScripts != null) {
                int version = foreignObject.version();
                if (version > 1) {
                    upgradeScripts.addAlterAddColumn(columnDefinition, tableName, version);
                }
            }
            columnDefinitions.add(columnDefinition);
        }
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
