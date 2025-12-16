package com.application.commons;

public enum CtWacherParametrosEnum {
    ID("id"),
    TIPO("tipo"), 
    CODIGO("codigo"),
    VALOR("valor");

    private final String columnName;

    CtWacherParametrosEnum(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnName() {
        return columnName;
    }
}