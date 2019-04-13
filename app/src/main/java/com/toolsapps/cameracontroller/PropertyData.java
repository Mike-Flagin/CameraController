package com.toolsapps.cameracontroller;

public class PropertyData {

    public static interface OnPropertyStateChangedListener {
        void onPropertyDescChanged(PropertyData property);
    }

    public static interface OnPropertyValueChangedListener {
        void onPropertyValueChanged(PropertyData property, int value);
    }

    private final int property;

    int[] values = new int[0];
    boolean enabled;
    int currentValue;
    int currentIndex;
    String[] labels;
    Integer[] icons;

    private OnPropertyStateChangedListener descChangedListener;
    private OnPropertyValueChangedListener valueChangedListener;

    public PropertyData(int property) {
        this.property = property;
        this.currentValue = -1;
        this.currentIndex = -1;
    }

    public void setOnPropertyDescChangedListener(OnPropertyStateChangedListener listener) {
        this.descChangedListener = listener;
    }

    public void setOnPropertyValueChangedListener(OnPropertyValueChangedListener listener) {
        this.valueChangedListener = listener;
    }

    public void setDescription(int[] values, String[] labels, Integer[] icons) {
        this.values = values;
        this.labels = labels;
        this.icons = icons;
        this.currentIndex = -1;
        if (descChangedListener != null) {
            descChangedListener.onPropertyDescChanged(this);
        }
    }

    public void setValue(int value) {
        this.currentValue = value;
        this.currentIndex = -1;
        if (valueChangedListener != null) {
            valueChangedListener.onPropertyValueChanged(this, value);
        }
    }

    public void setValueByIndex(int index) {
        this.currentValue = values[index];
        this.currentIndex = index;
    }

    public int calculateCurrentIndex() {
        if (currentIndex == -1) {
            for (int i = 0; i < values.length; ++i) {
                if (values[i] == currentValue) {
                    currentIndex = i;
                    break;
                }
            }
        }
        return currentIndex;
    }
}