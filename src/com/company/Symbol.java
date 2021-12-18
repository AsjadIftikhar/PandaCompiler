package com.company;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Symbol implements Serializable {
    String token;
    String type;
    String value;

    public Symbol(Symbol s){
        this.token = s.token;
        this.type = s.type;
        this.value = s.value;
    }

    public Symbol(String token, String type, String value) {
        this.token = token;
        this.type = type;
        this.value = value;
    }

    public Symbol() {

    }

    public void Print(ObjectOutputStream oos) throws IOException {
        oos.writeObject(this);
    }

    @Override
    public String toString(){
        return (type + "," + token + "," + value + "\n");
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
