package com.company;

import java.io.*;

public class PandaLex {

    private static BufferedReader br;
//    private static PrintWriter pw;
    private static ObjectOutputStream pw;

    public PandaLex(String filename) throws IOException {
        //FileReading for both read and write file
        File readFile = new File(filename + ".cc");
        File writeFile = new File(filename + ".out");

        br = new BufferedReader(new FileReader(readFile));
//        pw = new PrintWriter(writeFile);
        pw = new ObjectOutputStream(new FileOutputStream(writeFile));
    }

    public static void writer(Token token) throws IOException {
        System.out.print(token);
//        pw.write(token.toString());
        pw.writeObject(token);
    }

    public static boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    public static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    public static boolean isSeparator(char c) {
        return c == '(' || c == ')' || c == '{' || c == '}' || c == '[' || c == ']' || c == ';' || c == ',';
    }

    public static boolean isIdentifier(char c) {
        if (isLetter(c))
            return true;
        else if (isDigit(c))
            return true;
        else return c == '_';
    }

    public void Execute() throws IOException {
        //Character by character Loop till the end of .cc file
        int isEnd = 0;
        int currState = 0;
        char currChar = '\0';
        StringBuilder buffer = new StringBuilder();
        boolean isread = true;

        // Main While LOOP
        while (isEnd != -1) {
            if (isread) {
                isEnd = br.read();
                currChar = (char) isEnd;
            } else {
                isread = true;
            }
            switch (currState) {
                //Start State
                case 0:
                    if (currChar == 'i') {
                        currState = 1;
                        buffer.append(currChar);
                    } else if (currChar == 'c') {
                        currState = 2;
                        buffer.append(currChar);
                    } else if (currChar == 'e') {
                        currState = 3;
                        buffer.append(currChar);
                    } else if (currChar == 'p') {
                        currState = 4;
                        buffer.append(currChar);
                    } else if (currChar == 'w') {
                        currState = 5;
                        buffer.append(currChar);
                    } else if (currChar == '+') {
                        currState = 6;
                        buffer.append(currChar);
                    } else if (currChar == '-') {
                        currState = 7;
                        buffer.append(currChar);
                    } else if (currChar == '*') {
                        buffer.append(currChar);
                        Token token = new Token("MUL", "^");
                        writer(token);
                        buffer = new StringBuilder();
                    } else if (currChar == '/') {
                        currState = 8;
                        buffer.append(currChar);
                    } else if (currChar == '<') {
                        currState = 9;
                        buffer.append(currChar);
                    } else if (currChar == '>') {
                        currState = 10;
                        buffer.append(currChar);
                    } else if (currChar == '=') {
                        currState = 11;
                        buffer.append(currChar);
                    } else if (currChar == '~') {
                        currState = 12;
                        buffer.append(currChar);
                    } else if (currChar == ':') {
                        buffer.append(currChar);
                        Token token = new Token("ASSIGN", buffer.toString());
                        writer(token);
                        buffer = new StringBuilder();
                    } else if (isLetter(currChar)) {
                        currState = 13;
                        buffer.append(currChar);
                    } else if (isDigit(currChar)) {
                        currState = 14;
                        buffer.append(currChar);
                    } else if (currChar == '"') {
                        currState = 15;
                    } else if (isSeparator(currChar)) {
                        buffer.append(currChar);
                        Token token = new Token("SEPARATOR", buffer.toString());
                        writer(token);
                        buffer = new StringBuilder();
                    } else if (currChar == '\'' || currChar == '’') {
                        currState = 71;
                    }

                    break;


                //String starts with 'i'
                case 1:
                    if (currChar == 'n')
                        currState = 20;
                    else if (currChar == 'f')
                        currState = 21;
                    else {
                        currState = 13;
                        isread = false;
                    }

                    if (currState != 13)
                        buffer.append(currChar);
                    break;

                //String starts with 'c'
                case 2:
                    if (currChar == 'h')
                        currState = 22;
                    else {
                        currState = 13;
                        isread = false;
                    }

                    if (currState != 13)
                        buffer.append(currChar);
                    break;

                //String starts with 'e'
                case 3:
                    if (currChar == 'l')
                        currState = 23;
                    else {
                        currState = 13;
                        isread = false;
                    }

                    if (currState != 13)
                        buffer.append(currChar);
                    break;

                //String starts with 'p'
                case 4:
                    if (currChar == 'r')
                        currState = 24;
                    else {
                        currState = 13;
                        isread = false;
                    }

                    if (currState != 13)
                        buffer.append(currChar);
                    break;

                //String starts with 'w'
                case 5:
                    if (currChar == 'h')
                        currState = 25;
                    else {
                        currState = 13;
                        isread = false;
                    }

                    if (currState != 13)
                        buffer.append(currChar);
                    break;

                //String starts with '+'
                case 6:
                    if (currChar == '+') {
                        buffer.append(currChar);
                        Token token = new Token("INCR", "^");
                        writer(token);

                        currState = 0;
                        buffer = new StringBuilder();
                    } else {
                        Token token = new Token("ADD", "^");
                        writer(token);

                        currState = 0;
                        buffer = new StringBuilder();
                        isread = false;
                    }
                    break;

                //String starts with '-'
                case 7:
                    if (currChar == '>') {
                        buffer.append(currChar);
                        Token token = new Token("ARROW", "^");
                        writer(token);

                        currState = 0;
                        buffer = new StringBuilder();
                    } else {
                        Token token = new Token("SUB", "^");
                        writer(token);

                        currState = 0;
                        buffer = new StringBuilder();
                        isread = false;
                    }
                    break;

                //String starts with '/'
                case 8:
                    if (currChar == '/')
                        currState = 26;
                    else if (currChar == '*')
                        currState = 27;
                    else {
                        Token token = new Token("DIV", "^");
                        writer(token);

                        currState = 0;
                        buffer = new StringBuilder();
                        isread = false;
                    }
                    if (currState != 0)
                        buffer.append(currChar);
                    break;


                //String starts with '<'
                case 9:
                    if (currChar == '=') {
                        buffer.append(currChar);
                        Token token = new Token("RO", "LE");
                        writer(token);

                        currState = 0;
                        buffer = new StringBuilder();
                    } else {
                        Token token = new Token("RO", "LT");
                        writer(token);

                        currState = 0;
                        buffer = new StringBuilder();
                        isread = false;
                    }
                    break;

                //String starts with '>'
                case 10:
                    if (currChar == '=') {
                        buffer.append(currChar);
                        Token token = new Token("RO", "GE");
                        writer(token);

                        currState = 0;
                        buffer = new StringBuilder();
                    } else {
                        Token token = new Token("RO", "LT");
                        writer(token);

                        currState = 0;
                        buffer = new StringBuilder();
                        isread = false;
                    }
                    break;

                //String starts with '='
                case 11:
                    if (currChar == '=') {
                        buffer.append(currChar);
                        Token token = new Token("RO", "EQ");
                        writer(token);

                        currState = 0;
                        buffer = new StringBuilder();
                    } else {
                        Token token = new Token("ASSIGN", buffer.toString());
                        writer(token);

                        currState = 0;
                        buffer = new StringBuilder();
                        isread = false;
                    }
                    break;

                //String starts with '~'
                case 12:
                    if (currChar == '=') {
                        buffer.append(currChar);
                        Token token = new Token("RO", "NE");
                        writer(token);

                        currState = 0;
                        buffer = new StringBuilder();
                    } else {
                        System.out.println("Error code 2: Syntax Error");
                        System.exit(2);
                    }
                    break;

                //String starts with a letter
                case 13:
                    if (!isIdentifier(currChar)) {
                        Token token = new Token("ID", buffer.toString());
                        writer(token);

                        currState = 0;
                        buffer = new StringBuilder();
                        isread = false;
                    }
                    if (currState != 0)
                        buffer.append(currChar);
                    break;

                //String starts with a digit
                case 14:
                    if (!isDigit(currChar)) {
                        Token token = new Token("NUM", buffer.toString());
                        writer(token);

                        currState = 0;
                        buffer = new StringBuilder();
                        isread = false;
                    }
                    if (currState != 0)
                        buffer.append(currChar);
                    break;

                //String starts with a "
                case 15:
                    if (currChar == '"') {
                        Token token = new Token("STRING", buffer.toString());
                        writer(token);

                        currState = 0;
                        buffer = new StringBuilder();
                    }
                    if (currState != 0)
                        buffer.append(currChar);
                    break;

                //String starts with 'in'
                case 20:
                    if (currChar == 't') {
                        currState = 30;
                        buffer.append(currChar);
                    } else if (currChar == 'p') {
                        currState = 31;
                        buffer.append(currChar);
                    } else {
                        currState = 13;
                        isread = false;
                    }
                    break;

                //String starts with 'if'
                case 21:
                    if (isIdentifier(currChar))
                        currState = 13;
                    else {
                        Token token = new Token("IF", "^");
                        writer(token);

                        currState = 0;
                        buffer = new StringBuilder();
                        isread = false;
                    }
                    if (currState != 0)
                        buffer.append(currChar);
                    break;


                //String starts with 'ch'
                case 22:
                    if (currChar == 'a') {
                        currState = 32;
                        buffer.append(currChar);
                    } else {
                        currState = 13;
                        isread = false;
                    }
                    break;


                //String starts with 'el'
                case 23:
                    if (currChar == 'i') {
                        currState = 33;
                        buffer.append(currChar);
                    } else if (currChar == 's') {
                        currState = 34;
                        buffer.append(currChar);
                    } else {
                        currState = 13;
                        isread = false;
                    }
                    break;


                //String starts with 'pr'
                case 24:
                    if (currChar == 'i') {
                        currState = 35;
                        buffer.append(currChar);
                    } else {
                        currState = 13;
                        isread = false;
                    }
                    break;


                //String starts with 'wh'
                case 25:
                    if (currChar == 'i') {
                        currState = 36;
                        buffer.append(currChar);
                    } else {
                        currState = 13;
                        isread = false;
                    }
                    break;

                //String starts with '//'
                case 26:
                    if (currChar == '\n') {
                        Token token = new Token("COMMENT", buffer.toString());
                        writer(token);

                        currState = 0;
                        buffer = new StringBuilder();
                    }

                    if (currState != 0)
                        buffer.append(currChar);
                    break;


                //String starts with '/*'
                case 27:
                    if (currChar == '*') {
                        currState = 37;
                    }
                    buffer.append(currChar);
                    break;


                //String starts with 'int'
                case 30:
                    if (isIdentifier(currChar)) {
                        currState = 13;
                    } else {
                        Token token = new Token("INT", "^");
                        writer(token);

                        currState = 0;
                        buffer = new StringBuilder();
                    }
                    isread = false;
                    break;

                //String starts with 'inp'
                case 31:
                    if (currChar == 'u') {
                        currState = 40;
                        buffer.append(currChar);
                    } else {
                        currState = 13;
                        isread = false;
                    }
                    break;


                //String starts with 'cha'
                case 32:
                    if (currChar == 'r') {
                        currState = 41;
                        buffer.append(currChar);
                    } else {
                        currState = 13;
                        isread = false;
                    }
                    break;

                //String starts with 'eli'
                case 33:
                    if (currChar == 'f') {
                        currState = 42;
                        buffer.append(currChar);
                    } else {
                        currState = 13;
                        isread = false;
                    }
                    break;

                //String starts with 'els'
                case 34:
                    if (currChar == 'e') {
                        currState = 43;
                        buffer.append(currChar);
                    } else {
                        currState = 13;
                        isread = false;
                    }
                    break;

                //String starts with 'pri'
                case 35:
                    if (currChar == 'n') {
                        currState = 44;
                        buffer.append(currChar);
                    } else {
                        currState = 13;
                        isread = false;
                    }
                    break;

                //String starts with 'whi'
                case 36:
                    if (currChar == 'l') {
                        currState = 45;
                        buffer.append(currChar);
                    } else {
                        currState = 13;
                        isread = false;
                    }
                    break;

                //String(comment) end with *
                case 37:
                    if (currChar == '/') {
                        Token token = new Token("MCOMMENT", buffer.toString());
                        writer(token);

                        currState = 0;
                        buffer = new StringBuilder();
                    } else {
                        currState = 27;
                        isread = false;
                    }
                    break;


                //String starts with 'inpu'
                case 40:
                    if (currChar == 't') {
                        currState = 50;
                        buffer.append(currChar);
                    } else {
                        currState = 13;
                        isread = false;
                    }
                    break;

                //String starts with 'char'
                case 41:
                    if (isIdentifier(currChar)) {
                        currState = 13;
                    } else {
                        Token token = new Token("CHAR", "^");
                        writer(token);

                        currState = 0;
                        buffer = new StringBuilder();
                    }
                    isread = false;
                    break;

                //String starts with 'elif'
                case 42:
                    if (isIdentifier(currChar)) {
                        currState = 13;
                    } else {
                        Token token = new Token("ELIF", "^");
                        writer(token);

                        currState = 0;
                        buffer = new StringBuilder();
                    }
                    isread = false;
                    break;

                //String starts with 'else'
                case 43:
                    if (isIdentifier(currChar)) {
                        currState = 13;
                    } else {
                        Token token = new Token("ELSE", "^");
                        writer(token);

                        currState = 0;
                        buffer = new StringBuilder();
                    }
                    isread = false;
                    break;

                //String starts with 'prin'
                case 44:
                    if (currChar == 't') {
                        currState = 51;
                        buffer.append(currChar);
                    } else {
                        currState = 13;
                        isread = false;
                    }
                    break;

                //String starts with 'whil'
                case 45:
                    if (currChar == 'e') {
                        currState = 52;
                        buffer.append(currChar);
                    } else {
                        currState = 13;
                        isread = false;
                    }
                    break;


                //String starts with 'input'
                case 50:
                    if (isIdentifier(currChar)) {
                        currState = 13;
                    } else {
                        Token token = new Token("INPUT", "^");
                        writer(token);

                        currState = 0;
                        buffer = new StringBuilder();
                    }
                    isread = false;
                    break;

                //String starts with 'print'
                case 51:
                    if (currChar == 'l') {
                        currState = 60;
                        buffer.append(currChar);
                    } else if (isIdentifier(currChar)) {
                        currState = 13;
                        isread = false;
                    } else {
                        Token token = new Token("PRINT", "^");
                        writer(token);

                        currState = 0;
                        buffer = new StringBuilder();
                        isread = false;
                    }
                    break;

                //String start with 'while'
                case 52:
                    if (isIdentifier(currChar)) {
                        currState = 13;
                    } else {
                        Token token = new Token("WHILE", "^");
                        writer(token);

                        currState = 0;
                        buffer = new StringBuilder();
                    }
                    isread = false;
                    break;


                //String starting with 'printl'
                case 60:
                    if (currChar == 'n') {
                        currState = 70;
                        buffer.append(currChar);
                    } else if (isIdentifier(currChar)) {
                        currState = 13;
                        isread = false;
                    } else {
                        Token token = new Token("ID", buffer.toString());
                        writer(token);

                        currState = 0;
                        buffer = new StringBuilder();
                        isread = false;
                    }
                    break;

                //String starting with 'println'
                case 70:
                    if (isIdentifier(currChar)) {
                        currState = 13;
                    } else {
                        Token token = new Token("PRINTLN", "^");
                        writer(token);

                        currState = 0;
                        buffer = new StringBuilder();
                    }
                    isread = false;
                    break;

                //String starting with " ' "
                case 71:

                    buffer.append(currChar);
                    currState = 72;
                    break;

                // String with 'anychar
                case 72:
                    if (currChar == '\'' || currChar == '’') {
                        Token token = new Token("CHARL", buffer.toString());
                        writer(token);

                        currState = 0;
                        buffer = new StringBuilder();
                    } else {
                        System.out.println("Incorrect Character Literal");
                        System.exit(3);
                    }

                    break;
                //END
            }
        }
        pw.flush();
        pw.close();
        br.close();

    }
}

