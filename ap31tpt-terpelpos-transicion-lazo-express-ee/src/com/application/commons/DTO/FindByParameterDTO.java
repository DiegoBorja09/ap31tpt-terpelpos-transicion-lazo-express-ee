package com.application.commons.DTO;

public class FindByParameterDTO {
    private final String parameterColumn;
    private final String value;

    public FindByParameterDTO(String parameterColumn, String value) {
        this.parameterColumn = parameterColumn;
        this.value = value;
    }

    public String getParameterColumn() {
        return parameterColumn;
    }

    public String getValue() {
        return value;
    }
}
