package com.application.core;

public interface BaseUseCasesWithParams<I,O> {
    O execute(I i);
}
