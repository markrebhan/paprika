package com.mrebhan.paprika;

public final class SqlAlterAddColumnStatement {

    private ColumnDefinition column;
    private String tableName;

    public SqlAlterAddColumnStatement(ColumnDefinition column, String tableName) {
        this.column = column;
        this.tableName = tableName;
    }

    @Override
    public String toString() {
        return "ALTER TABLE " + tableName + " ADD COLUMN " + column.toString();
    }
}
