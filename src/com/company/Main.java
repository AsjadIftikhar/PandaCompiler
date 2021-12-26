package com.company;

import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
//        To Manually Input the file name change this filename variable
//        filename should not include extension as .cc is assumed
//        If compiling in windows make sure the file doesn't have the .txt extension
//        Output extension is .out
        String filename = "Main";
        PandaLex pandaLex = new PandaLex(filename);
        pandaLex.Execute();

        System.out.println("-------------------- Parser -------------------------------");
        System.out.println("-------------------- Starts -------------------------------");
        System.out.println("-------------------- Here -------------------------------");

        PandaParser pp = new PandaParser(filename);
        pp.makeParser();

    }
}