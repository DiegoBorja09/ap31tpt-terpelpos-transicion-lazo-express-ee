package com.application.core;

public abstract  class AbstractUseCase<T> {
    public T execute() {
        String className = this.getClass().getSimpleName();
        System.out.println("Ejecutando - caso de uso " + className);
        return run();
    }

    protected abstract T run();

}
