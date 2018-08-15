package com.moselo.HomingPigeon.Model;

public class EmitModel<T> {

    private int emitType;
    private T message;

    public int getEmitType() {
        return emitType;
    }

    public void setEmitType(int emitType) {
        this.emitType = emitType;
    }

    public T getMessage() {
        return message;
    }

    public void setMessage(T message) {
        this.message = message;
    }
}
