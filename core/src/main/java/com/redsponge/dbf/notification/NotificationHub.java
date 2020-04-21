package com.redsponge.dbf.notification;

import com.badlogic.gdx.utils.Array;

public class NotificationHub<ValueType> {

    private ValueType value;

    private Array<IValueNotified<ValueType>> listeners;

    public NotificationHub() {
        listeners = new Array<>();
    }

    public NotificationHub(ValueType defaultValue) {
        this();
        this.value = defaultValue;
    }

    public void addListener(IValueNotified<ValueType> listener) {
        listeners.add(listener);
    }

    public void removeListener(IValueNotified<ValueType> listener) {
        listeners.removeValue(listener, true);
    }

    public void updateValue(ValueType value) {
        this.value =value;
        notifyListeners();
    }

    private void notifyListeners() {
        for (int i = 0; i < listeners.size; i++) {
            listeners.get(i).update(value);
        }
    }

    public ValueType getValue() {
        return value;
    }
}
