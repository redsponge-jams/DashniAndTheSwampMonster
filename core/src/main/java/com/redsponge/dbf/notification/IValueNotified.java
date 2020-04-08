package com.redsponge.dbf.notification;

@FunctionalInterface
public interface IValueNotified<ValueType> {

    void update(ValueType newValue);

}
