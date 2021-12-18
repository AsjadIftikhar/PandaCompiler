package com.company;

import java.io.Serializable;

public class Token implements Serializable {
    String name;
    String lexeme;

    public Token(){}

    public Token(String name, String lexeme) {
        this.name = name;
        this.lexeme = lexeme;
    }

    public Boolean isEqual(String tok){
        return (this.name.equals(tok));
    }

    public Boolean isLexEqual(String tok){
        return (this.lexeme.equals(tok));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLexeme() {
        return lexeme;
    }

    public void setLexeme(String lexeme) {
        this.lexeme = lexeme;
    }

    @Override
    public String toString() {
        return "(" + name + ", " + "'" + lexeme + "'" + ")\n";
    }
}
