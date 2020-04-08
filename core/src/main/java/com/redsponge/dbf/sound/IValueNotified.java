package com.redsponge.dbf.sound;

@FunctionalInterface
public interface IValueNotified<ValueType> {

    void update(ValueType newValue);

}
