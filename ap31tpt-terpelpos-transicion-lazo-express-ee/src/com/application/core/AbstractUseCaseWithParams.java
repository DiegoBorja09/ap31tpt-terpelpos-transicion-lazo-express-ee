package com.application.core;

public abstract class AbstractUseCaseWithParams<I, O> {
    public O execute(I input) {
        String className = this.getClass().getSimpleName();
        System.out.println("Ejecutando - caso de uso " + className + " con parámetros");
        return run(input);
    }

    protected abstract O run(I input);
}