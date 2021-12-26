package com.company;

import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

public class PandaVM {
    private static ObjectInputStream ois3AC;

    private HashMap<Integer, String> _3AddressCode;
    private HashMap<String, Symbol> symbolTable;

    public PandaVM(String filename) throws IOException {
        _3AddressCode = new HashMap<>();
        codeFromFile(filename);
    }

    public void codeFromFile(String filename) throws IOException {
        File readFile = new File(filename + ".out");
        ois3AC = new ObjectInputStream(new FileInputStream(readFile));

        while (true) {
            try {
                ThreeAddressCode threeAddressCode = (ThreeAddressCode) ois3AC.readObject();
                _3AddressCode.put(threeAddressCode.getLineNumber(), threeAddressCode.getCode());
            } catch (Exception e) {
                return;
            }
        }
    }

    public PandaVM(HashMap<Integer, String> Address3Code, HashMap<String, Symbol> symboltable) throws IOException {
        _3AddressCode = Address3Code;
        symbolTable = symboltable;

    }
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
    public Symbol whatAreYou(String id) {
        //First check symbol Table
        if (symbolTable.get(id) != null) {
            return symbolTable.get(id);
        }
        else {
            //Check if number or char
            Symbol temp;
            if (isNumeric(id)) {
                temp = new Symbol("IamInt", "INT", id);
            }
            else {
                temp = new Symbol("IamChar", "CHAR", id);
            }
            return temp;
        }
    }

    public void run() throws IOException {

        for (int pc = 0; pc < _3AddressCode.size(); pc++) {
//            System.out.println((pc) + ": " + _3AddressCode.get(pc));
            String line = _3AddressCode.get(pc);

            if (line.startsWith("out")){
                if (line.contains("'")){
                    System.out.print(line.substring(5, line.length() - 1));
                }
                else if (line.contains("end-line")){
                    System.out.println();
                }
                else {
                    System.out.print(symbolTable.get(line.substring(4)).getValue());
                }
            }
            else if (line.startsWith("in")) {
                Scanner sc = new Scanner(System.in);

                Symbol symbol = symbolTable.get(line.substring(3));
                if (symbol.getType().equals("INT")) {
                    int a = sc.nextInt();
                    symbol.setValue(String.valueOf(a));
                }
                else {
                    char b = sc.next().charAt(0);
                    symbol.setValue(String.valueOf(b));
                }
                symbolTable.put(symbol.getToken(), symbol);
            }
            else if (line.startsWith("goto")) {
                int next_instruction = Integer.parseInt(line.substring(5).strip());
                pc = next_instruction - 1;
            }
            //Format: c = a + b
            else if (line.contains("+") || line.contains("-") || line.contains("*") || line.contains("/")) {

                String [] remove_equal = line.split("=");
                String [] remove_op = remove_equal[1].split("[+\\-*/]");
                String c = remove_equal[0].strip();
                String a = remove_op[0].strip();
                String b = remove_op[1].strip();

                int int_a, int_b, int_c = 0;
                //First check if either one is literal or not
                if (isNumeric(a)) {
                    int_a = Integer.parseInt(a);
                }
                else {
                    int_a = Integer.parseInt(symbolTable.get(a).getValue());
                }
                if (isNumeric(b)) {
                    int_b = Integer.parseInt(b);
                }
                else {
                    int_b = Integer.parseInt(symbolTable.get(b).getValue());
                }

                if (line.contains("+")) {
                    int_c = int_a + int_b;
                }
                else if (line.contains("-")) {
                    int_c = int_a - int_b;
                }
                else if (line.contains("*")) {
                    int_c = int_a * int_b;
                }
                else if (line.contains("/")) {
                    int_c = int_a / int_b;
                }
                Symbol s = new Symbol(c, "INT", String.valueOf(int_c));
                symbolTable.put(c, s);
            }
            //Format: a = b
            else if (line.contains("=")) {
                String [] remove_equal = line.split("=");
                String a = remove_equal[0].strip();
                String b = remove_equal[1].strip();

                int int_b;
                String str_b;
                Symbol s = symbolTable.get(a);
                if (s.getType().equals("INT")) {
                    if (isNumeric(b)) {
                        int_b = Integer.parseInt(b);
                    }
                    else {
                        int_b = Integer.parseInt(symbolTable.get(b).getValue());
                    }
                    s.setValue(String.valueOf(int_b));
                    symbolTable.replace(a, s);

                }
                else {
                    if (b.contains("'")) {
                        str_b = b.replace("'", "");
                    }
                    else {
                        str_b = s.getValue();
                    }
                    s.setValue(str_b);
                    symbolTable.replace(a, s);
                }
            }
            //Format: while/if a OP b goto x
            else if (line.startsWith("While") || line.startsWith("If")) {
                line = line.replace("While", "");
                line = line.replace("If", "");

                String [] toks = line.strip().split(" ");
                String a = toks[0].strip();
                String OP = toks[1].strip();
                String b = toks[2].strip();
                String x = toks[4].strip();

                Symbol symbol_a = whatAreYou(a);
                Symbol symbol_b = whatAreYou(b);

                if (symbol_a.getType().equals("INT") && symbol_b.getType().equals("INT")) {
                    int int_a = Integer.parseInt(symbol_a.getValue());
                    int int_b = Integer.parseInt(symbol_b.getValue());

                    switch (OP) {
                        case "LE":
                            if (int_a <= int_b) {
                                int next_instruction = Integer.parseInt(x);
                                pc = next_instruction - 1;
                            }

                            break;
                        case "LT":
                            if (int_a < int_b) {
                                int next_instruction = Integer.parseInt(x);
                                pc = next_instruction - 1;
                            }
                            break;
                        case "GE":
                            if (int_a >= int_b) {
                                int next_instruction = Integer.parseInt(x);
                                pc = next_instruction - 1;
                            }
                            break;
                        case "GT":
                            if (int_a > int_b) {
                                int next_instruction = Integer.parseInt(x);
                                pc = next_instruction - 1;
                            }
                            break;
                        case "EQ":
                            if (int_a == int_b) {
                                int next_instruction = Integer.parseInt(x);
                                pc = next_instruction - 1;
                            }

                            break;
                        case "NE":
                            if (int_a != int_b) {
                                int next_instruction = Integer.parseInt(x);
                                pc = next_instruction - 1;
                            }

                            break;
                    }
                }
                else if (symbol_a.getType().equals("CHAR") && symbol_b.getType().equals("CHAR")) {
                    char char_a = symbol_a.getValue().charAt(0);
                    char char_b = symbol_b.getValue().charAt(0);

                    switch (OP) {
                        case "LE":
                            if (char_a <= char_b) {
                                int next_instruction = Integer.parseInt(x);
                                pc = next_instruction - 1;
                            }

                            break;
                        case "LT":
                            if (char_a < char_b) {
                                int next_instruction = Integer.parseInt(x);
                                pc = next_instruction - 1;
                            }
                            break;
                        case "GE":
                            if (char_a >= char_b) {
                                int next_instruction = Integer.parseInt(x);
                                pc = next_instruction - 1;
                            }
                            break;
                        case "GT":
                            if (char_a > char_b) {
                                int next_instruction = Integer.parseInt(x);
                                pc = next_instruction - 1;
                            }
                            break;
                        case "EQ":
                            if (char_a == char_b) {
                                int next_instruction = Integer.parseInt(x);
                                pc = next_instruction - 1;
                            }

                            break;
                        case "NE":
                            if (char_a != char_b) {
                                int next_instruction = Integer.parseInt(x);
                                pc = next_instruction - 1;
                            }

                            break;
                    }
                }
                else {
                    System.out.println("Mismatch Types");
                    System.exit(5);
                }
            }
        }

    }
}
