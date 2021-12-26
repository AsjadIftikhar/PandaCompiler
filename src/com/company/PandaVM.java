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
    public void run() {

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
                int next_instruction = Integer.parseInt(line.substring(5));
                pc = next_instruction - 1;
            }
            //Format: c = a + b
            else if (line.contains("+")) {

            }
            else if (line.contains("-")) {

            }
            else if (line.contains("*")) {

            }
            else if (line.contains("/")) {

            }
        }

    }
}
