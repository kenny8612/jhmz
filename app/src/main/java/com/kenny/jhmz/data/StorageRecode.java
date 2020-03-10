package com.kenny.jhmz.data;

import io.realm.RealmObject;

public class StorageRecode extends RealmObject {
    private String barcode;
    private String name;
    private int in_storage_number;
    private int out_storage_number;
    private int total_storage_number;
    private int operator_type;
    private long time;

    public static final int OPERATOR_TYPE_IN = 0;
    public static final int OPERATOR_TYPE_OUT = 1;
    public static final int OPERATOR_TYPE_DELETE = 2;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getIn_storage_number() {
        return in_storage_number;
    }

    public void setIn_storage_number(int in_storage_number) {
        this.in_storage_number = in_storage_number;
    }

    public int getOut_storage_number() {
        return out_storage_number;
    }

    public void setOut_storage_number(int out_storage_number) {
        this.out_storage_number = out_storage_number;
    }

    public int getOperator_type() {
        return operator_type;
    }

    public void setOperator_type(int operator_type) {
        this.operator_type = operator_type;
    }

    public int getTotal_storage_number() {
        return total_storage_number;
    }

    public void setTotal_storage_number(int total_storage_number) {
        this.total_storage_number = total_storage_number;
    }
}
