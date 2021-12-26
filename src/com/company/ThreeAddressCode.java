package com.company;

import java.io.Serializable;

public class ThreeAddressCode implements Serializable {
    private int lineNumber;
    private String code;

    public ThreeAddressCode(){}

    public ThreeAddressCode(int lineNumber, String code) {
        this.lineNumber = lineNumber;
        this.code = code;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return (lineNumber + ": " + code);
    }
}
