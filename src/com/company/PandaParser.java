package com.company;

import java.io.*;
import java.util.HashMap;


public class PandaParser {

    private static Token currToken;
    private static ObjectInputStream ois;
    private Symbol temp;
    private static int tabs;
    HashMap<String, Symbol> symbolTable;
    private static FileWriter pw;


    public PandaParser(String filename) throws IOException {
        File readFile = new File(filename + ".out");
        ois = new ObjectInputStream(new FileInputStream(readFile));

        temp = new Symbol();
        symbolTable = new HashMap<String, Symbol>();
        tabs = 0;
    }

    public void printTabs() throws IOException {
        System.out.print('|');
        pw.write("|");
        for (int i = 0; i < tabs; i++) {
            System.out.print("__");
            pw.write("__");
        }

    }

    public void Match(String expected_Token) throws IOException, ClassNotFoundException {
        if (currToken.getName().equals("COMMENT") || currToken.getName().equals("MCOMMENT")) {
            currToken = nextToken();
        } else if (expected_Token.equals(currToken.getName())) {
            if (currToken.getLexeme().equals("^")) {
                System.out.println(currToken.getName());
                pw.write(currToken.getName() + "\n");
            } else {
                System.out.println(currToken.getName() + "( " + currToken.getLexeme() + " )");
                pw.write(currToken.getName() + "( " + currToken.getLexeme() + " )\n");
            }


            currToken = nextToken();

        } else {
            System.out.println("BAD TOKEN");
            System.exit(3);
        }
    }

    public void Match_Lexeme(String expected_Token) throws IOException, ClassNotFoundException {
        if (expected_Token.equals(currToken.getLexeme())) {
            System.out.println(currToken.getLexeme());
            pw.write(currToken.getLexeme() + "\n");
            currToken = nextToken();
        } else {
            System.out.println("BAD TOKEN");
            System.exit(3);
        }
    }


    //Global CFG's
    private void isLITERAL() throws IOException, ClassNotFoundException {
        tabs++;

        if (currToken.getName().equals("NUM") || currToken.getName().equals("STRING") || currToken.getName().equals("CHARL")) {
            printTabs();
            pw.write(currToken.getName() + " ( " + currToken.getLexeme() + ")\n");
            currToken = nextToken();
        } else if (currToken.getName().equals("SUB")) {
            currToken = nextToken();
            if (currToken.getName().equals("NUM")) {
                printTabs();
                pw.write(currToken.getName() + " ( -" + currToken.getLexeme() + ")\n");
                currToken = nextToken();
            } else {
                System.out.println("BAD TOKEN");
                System.exit(3);
            }
        } else {
            System.out.println("BAD TOKEN");
            System.exit(3);
        }

        tabs--;
    }

    private Boolean isLITERALBOOL() throws IOException, ClassNotFoundException {
        if (currToken.isEqual("NUM")) {
            return true;
        } else if (currToken.isEqual("STRING")) {
            return true;
        } else if (currToken.isEqual("CHARL")) {
            return true;
        } else if (currToken.isEqual("SUB")) {
            return nextToken().isEqual("NUM");
        } else {
            return false;
        }
    }


    //Start of CFG
    void Start() throws IOException, ClassNotFoundException {
        if (currToken != null) {
            if (currToken.isEqual("COMMENT")) {
                Match("COMMENT");
                Start();
            } else if (currToken.isEqual("MCOMMENT")) {
                Match("MCOMMENT");
                Start();
            } else {
                if (currToken.isEqual("INT") || currToken.isEqual("CHAR")) {
                    System.out.println("VDI()");
                    pw.write("VDI()\n");
                    VDI();
                    Start();
                } else if (currToken.isEqual("PRINT") || currToken.isEqual("PRINTLN") || currToken.isEqual("INPUT")) {
                    System.out.println("PS()");
                    pw.write("PS()\n");
                    PS();
                    Start();
                } else if (currToken.isEqual("WHILE") || currToken.isEqual("IF")) {
                    System.out.println("CS()");
                    pw.write("CS()\n");
                    CS();
                    Start();
                } else if (currToken.isEqual("ID")) {
                    System.out.println("AS()");
                    pw.write("AS()\n");
                    AS();
                    Start();
                } else {
                    System.out.println("AO()");
                    pw.write("AO()\n");
                    int AOv = AO();
                    printTabs();
                    Match_Lexeme(";");
                    System.out.println(AOv);
                    Start();
                }
            }
        }

    }

    //DATA TYPES DECLARATION AND INITIALIZATION START

    private void VDI() throws IOException, ClassNotFoundException {
        tabs++;
        if (currToken.isEqual("INT")) {

            printTabs();
            Match("INT");
            temp.setType("INT");
            printTabs();
            Match_Lexeme(":");
            printTabs();
            temp.setToken(currToken.getLexeme());
            Match("ID");
            printTabs();
            System.out.println("IOP()");
            pw.write("IOP()\n");
            IOP();
            printTabs();
            Match_Lexeme(";");

        } else if (currToken.isEqual("CHAR")) {
            printTabs();
            Match("CHAR");
            temp.setType("CHAR");
            printTabs();
            Match_Lexeme(":");
            printTabs();
            temp.setToken(currToken.getLexeme());
            Match("ID");
            printTabs();
            System.out.println("COP()");
            pw.write("COP()\n");
            COP();
            printTabs();
            Match_Lexeme(";");

        }

        tabs--;
    }

    private void IOP() throws IOException, ClassNotFoundException {
        tabs++;

        if (currToken.isLexEqual("=")) {
            printTabs();
            Match_Lexeme("=");
            printTabs();
            System.out.println("AO()");
            pw.write("AO()\n");
            int AOv = AO();
            temp.setValue(String.valueOf(AOv));
            symbolTable.put(temp.getToken(), temp);
            temp = new Symbol();
            printTabs();
            System.out.println("IC()");
            pw.write("IC()\n");
            IC();

        } else if (currToken.isLexEqual(",")) {
            printTabs();
            System.out.println("IC()");
            pw.write("IC()\n");
            IC();
        } else if (currToken.isLexEqual(";")) {

            symbolTable.put(temp.getToken(), temp);
            temp = new Symbol();
        }


        tabs--;
    }

    private void IC() throws IOException, ClassNotFoundException {
        tabs++;

        if (currToken.isLexEqual(",")) {
            printTabs();
            Match_Lexeme(",");
            temp.setType("INT");
            temp.setToken(currToken.getLexeme());
            printTabs();
            Match("ID");
            printTabs();
            System.out.println("IOP()");
            pw.write("IOP()\n");
            IOP();
        }

        tabs--;
    }

    private void COP() throws IOException, ClassNotFoundException {
        tabs++;

        if (currToken.isLexEqual("=")) {
            printTabs();
            Match_Lexeme("=");
            printTabs();
            if (currToken.getName().equals("CHARL")) {
                temp.setValue(currToken.getLexeme());
                symbolTable.put(temp.getToken(), temp);
                temp = new Symbol();
                Match("CHARL");
            } else if (currToken.getName().equals("ID")) {
                Symbol symbol = symbolTable.get(currToken.getLexeme());
                if (symbol == null) {
                    System.out.println("Assignment before declaration..");
                    System.exit(4);
                }
                temp.setValue(symbol.getValue());
                symbolTable.put(temp.getToken(), temp);
                temp = new Symbol();
                Match("ID");
            }

            printTabs();
            System.out.println("CC()");
            pw.write("CC()\n");
            CC();

        } else if (currToken.isLexEqual(",")) {
            printTabs();
            System.out.println("CC()");
            pw.write("CC()\n");
            CC();
        } else if (currToken.isLexEqual(";")) {
            symbolTable.put(temp.getToken(), temp);
            temp = new Symbol();
        }

        tabs--;
    }

    private void CC() throws IOException, ClassNotFoundException {
        tabs++;

        if (currToken.isLexEqual(",")) {
            printTabs();
            Match_Lexeme(",");

            symbolTable.put(temp.getToken(), temp);
            temp = new Symbol(null, temp.getType(), null);

            printTabs();
            Match("ID");
            printTabs();
            System.out.println("COP()");
            pw.write("COP()\n");
            COP();

        }

        tabs--;
    }

    //DATA TYPES END


    // I/O STATEMENTS START

    private void PS() throws IOException, ClassNotFoundException {
        tabs++;

        if (currToken.isEqual("PRINT") || currToken.isEqual("PRINTLN")) {
            printTabs();
            System.out.println("P()");
            pw.write("P()\n");
            P();
            printTabs();
            Match_Lexeme("(");
            printTabs();
            System.out.println("POP()");
            pw.write("POP()\n");
            POP();
            printTabs();
            Match_Lexeme(")");
            printTabs();
            Match_Lexeme(";");

        } else if (currToken.isEqual("INPUT")) {
            printTabs();
            System.out.println("INP()");
            pw.write("INP()\n");
            INP();
            printTabs();
            Match_Lexeme(";");
        }

        tabs--;
    }

    private void INP() throws IOException, ClassNotFoundException {
        tabs++;

        printTabs();
        Match("INPUT");
        printTabs();
        Match("ARROW");
        printTabs();
        Match("ID");

        tabs--;
    }

    private void P() throws IOException, ClassNotFoundException {
        tabs++;

        if (currToken.isEqual("PRINT")) {
            printTabs();
            Match("PRINT");
        } else if (currToken.isEqual("PRINTLN")) {
            printTabs();
            Match("PRINTLN");
        } else {
            System.out.println("BAD TOKEN");
            System.exit(3);
        }

        tabs--;
    }

    private void POP() throws IOException, ClassNotFoundException {
        tabs++;

        if (currToken.isEqual("ID")) {
            printTabs();
            Match("ID");
        } else if (isLITERALBOOL()) {
            printTabs();
            System.out.println("LITERAL()");
            pw.write("LITERAL()\n");
            isLITERAL();
        } else {
            printTabs();
            System.out.println("AO()");
            pw.write("AO()\n");
            AO();
        }

        tabs--;
    }

    //I/O STATEMENTS END


    //CONTROL STATEMENTS START

    private void CS() throws IOException, ClassNotFoundException {
        tabs++;

        if (currToken.isEqual("WHILE") || currToken.isEqual("IF")) {
            printTabs();
            System.out.println("CK()");
            pw.write("CK()\n");
            CK();
            printTabs();
            System.out.println("EXP()");
            pw.write("EXP()\n");
            EXP();
            printTabs();
            Match_Lexeme(":");
            printTabs();
            Match_Lexeme("{");
            printTabs();
            System.out.println("ST()");
            pw.write("ST()\n");
            ST();
            printTabs();
            Match_Lexeme("}");
            printTabs();
            System.out.println("ES()");
            pw.write("ES()\n");
            ES();
        }

        tabs--;
    }

    private void CK() throws IOException, ClassNotFoundException {
        tabs++;

        if (currToken.isEqual("WHILE")) {
            printTabs();
            Match("WHILE");
        } else if (currToken.isEqual("IF")) {
            printTabs();
            Match("IF");
        }

        tabs--;
    }

    private void EXP() throws IOException, ClassNotFoundException {
        tabs++;

        printTabs();
        System.out.println("EV()");
        pw.write("EV()\n");
        EV();
        printTabs();
        Match("RO");
        printTabs();
        System.out.println("EV()");
        pw.write("EV()\n");
        EV();

        tabs--;
    }

    private void EV() throws IOException, ClassNotFoundException {
        tabs++;

        if (currToken.isEqual("ID")) {
            printTabs();
            Match("ID");
        } else {
            printTabs();
            System.out.println("LITERAL()");
            pw.write("LITERAL()\n");
            isLITERAL();
        }

        tabs--;
    }

    private void ST() throws IOException, ClassNotFoundException {
        tabs++;

        if (currToken.isEqual("INT") || currToken.isEqual("CHAR")) {
            printTabs();
            System.out.println("VDI()");
            pw.write("VDI()\n");
            VDI();
            printTabs();
            System.out.println("ST()");
            pw.write("ST()\n");
            ST();
        } else if (currToken.isEqual("PRINT") || currToken.isEqual("PRINTLN") || currToken.isEqual("INPUT")) {
            printTabs();
            System.out.println("PS()");
            pw.write("PS()\n");
            PS();
            printTabs();
            System.out.println("ST()");
            pw.write("ST()\n");
            ST();
        } else if (currToken.isEqual("ID") || currToken.isEqual("NUM") || currToken.isLexEqual("(") || currToken.isEqual("SUB")) {
            printTabs();
            System.out.println("AS()");
            pw.write("AS()\n");
            AS();
            printTabs();
            System.out.println("ST()");
            pw.write("ST()\n");
            ST();
        }

        tabs--;
    }

    private void ES() throws IOException, ClassNotFoundException {
        tabs++;
        if (currToken != null) {
            if (currToken.isEqual("ELIF")) {
                printTabs();
                Match("ELIF");
                printTabs();
                System.out.println("EXP()");
                pw.write("EXP()\n");
                EXP();
                printTabs();
                Match_Lexeme(":");
                printTabs();
                Match_Lexeme("{");
                printTabs();
                System.out.println("ST()");
                pw.write("ST()\n");
                ST();
                printTabs();
                Match_Lexeme("}");
                printTabs();
                System.out.println("ES()");
                pw.write("ES()\n");
                ES();
            } else if (currToken.isEqual("ELSE")) {
                printTabs();
                Match("ELSE");
                printTabs();
                Match_Lexeme(":");
                printTabs();
                Match_Lexeme("{");
                printTabs();
                System.out.println("ST()");
                pw.write("ST()\n");
                ST();
                printTabs();
                Match_Lexeme("}");
            }
        }


        tabs--;
    }

    //CONTROL STATEMENTS START


    //ARTHEMETIC STATEMENTS START

    private int AO() throws IOException, ClassNotFoundException {
        tabs++;
        printTabs();
        System.out.println("T()");
        pw.write("T()\n");
        int Tv = T();
        printTabs();
        System.out.println("EDASH()");
        pw.write("EDASH()\n");
        int AOv = EDASH(Tv);

        tabs--;
        return AOv;
    }

    private int EDASH(int EdashI) throws IOException, ClassNotFoundException {
        tabs++;


        if (currToken.isEqual("ADD")) {
            printTabs();
            Match("ADD");
            printTabs();
            System.out.println("T()");
            pw.write("T()\n");
            int Tv = T();
            EdashI += Tv;
            printTabs();
            System.out.println("EDASH()");
            pw.write("EDASH()\n");
            int EdashS = EDASH(EdashI);

            tabs--;
            return EdashS;

        } else if (currToken.isEqual("SUB")) {
            printTabs();
            Match("SUB");
            printTabs();
            System.out.println("T()");
            pw.write("T()\n");
            int Tv = T();
            EdashI -= Tv;
            printTabs();
            System.out.println("EDASH()");
            pw.write("EDASH()\n");
            int EdashS = EDASH(EdashI);

            tabs--;
            return EdashS;

        } else {
            tabs--;
            return EdashI;
        }

    }

    private int T() throws IOException, ClassNotFoundException {
        tabs++;

        printTabs();
        System.out.println("F()");
        pw.write("F()\n");
        int Fv = F();
        printTabs();
        System.out.println("TDASH()");
        pw.write("TDASH()\n");

        int TdashS = TDASH(Fv);

        tabs--;
        return TdashS;


    }

    private int TDASH(int TdashI) throws IOException, ClassNotFoundException {
        tabs++;

        if (currToken.isEqual("MUL")) {
            printTabs();
            Match("MUL");
            printTabs();
            System.out.println("F()");
            pw.write("F()\n");
            int Fv = F();
            TdashI *= Fv;
            printTabs();
            System.out.println("TDASH()");
            pw.write("TDASH()\n");
            int TdashS = TDASH(TdashI);

            tabs--;
            return TdashS;
        } else if (currToken.isEqual("DIV")) {
            printTabs();
            Match("DIV");
            printTabs();
            System.out.println("F()");
            pw.write("F()\n");
            int Fv = F();
            TdashI /= Fv;
            printTabs();
            System.out.println("TDASH()");
            pw.write("TDASH()\n");
            int TdashS = TDASH(TdashI);

            tabs--;
            return TdashS;
        } else {
            tabs--;
            return TdashI;
        }


    }

    private int F() throws IOException, ClassNotFoundException {
        tabs++;

        printTabs();
        System.out.println("Final()");
        pw.write("Final()\n");
        int PdashI = Final();
        printTabs();
        System.out.println("PDASH()");
        pw.write("PDASH()\n");
        int PdashS = PDASH(PdashI);

        tabs--;
        return PdashS;
    }

    private int PDASH(int PdashI) throws IOException, ClassNotFoundException {
        tabs++;

        if (currToken.isEqual("INCR")) {
            printTabs();
            Match("INCR");
            printTabs();
            System.out.println("Final()");
            pw.write("Final()\n");
            int FinalV = Final();
            PdashI = FinalV + 1;
            printTabs();
            System.out.println("PDASH()");
            pw.write("PDASH()\n");
            int PdashS = PDASH(PdashI);

            tabs--;
            return PdashS;

        } else {
            tabs--;
            return PdashI;
        }

    }

    private int Final() throws IOException, ClassNotFoundException {
        tabs++;

        if (currToken.isEqual("ID")) {
            printTabs();
            Symbol symbol = symbolTable.get(currToken.getLexeme());
            int FinalV = 0;
            if (symbol != null) {
                FinalV = Integer.parseInt(symbol.getValue());
            } else {
                System.out.println("Variable accessed before Declaration...");
                System.exit(4);
            }
            Match("ID");

            tabs--;
            return FinalV;
        } else if (currToken.isEqual("NUM")) {
            printTabs();
            int FinalV = Integer.parseInt(currToken.getLexeme());
            Match("NUM");

            tabs--;
            return FinalV;
        } else if (currToken.getName().equals("SUB")) {
            printTabs();
            Match("SUB");
            if (currToken.getName().equals("NUM")) {
                int FinalV = Integer.parseInt(currToken.getLexeme());
                Match("NUM");

                tabs--;
                return -FinalV;
            } else {
                System.out.println("BAD TOKEN");
                System.exit(3);
            }
        } else if (currToken.isLexEqual("(")) {
            printTabs();
            Match_Lexeme("(");
            printTabs();
            System.out.println("AO()");
            pw.write("AO()\n");
            int AOv = AO();
            printTabs();
            Match_Lexeme(")");

            tabs--;
            return AOv;


        } else {
            System.out.println("BAD TOKEN");
            System.exit(3);
        }
        return -1;
    }

    //ARTHEMETIC STATEMENTS END


    //ASSIGNMENT STATEMENT START
    private void AS() throws IOException, ClassNotFoundException {
        tabs++;

        if (currToken.isEqual("ID")) {
            printTabs();
            Symbol symbol = symbolTable.get(currToken.getLexeme());
            if (symbol == null) {
                System.out.println("Assignment before declaration..");
                System.exit(4);
            }
            Match("ID");
            if (currToken.isLexEqual("=")) {
                printTabs();
                Match_Lexeme("=");

                if (symbol.getType().equals("CHAR")) {
                    if (currToken.isEqual("CHARL")) {
                        printTabs();
                        symbol.setValue(currToken.getLexeme());
                        symbolTable.replace(symbol.getToken(), symbol);
                        Match("CHARL");
                    } else if (currToken.isEqual("STRING")) {
                        printTabs();
                        Match("STRING");
                    } else if (currToken.isEqual("ID")) {
                        Symbol rhs = symbolTable.get(currToken.getLexeme());
                        if (rhs == null) {
                            System.out.println("Assignment before declaration..");
                            System.exit(4);
                        }
                        symbol.setValue(rhs.getValue());
                        symbolTable.replace(symbol.getToken(), symbol);
                        Match("ID");
                    }
                } else {
                    printTabs();
                    System.out.println("AO()");
                    pw.write("AO()\n");
                    int AOv = AO();
                    symbol.setValue(String.valueOf(AOv));
                    symbolTable.replace(symbol.getToken(), symbol);
                }

            } else if (currToken.isEqual("INCR")) {
                printTabs();
                symbol.setValue(String.valueOf(Integer.parseInt(symbol.getValue()) + 1));
                symbolTable.replace(symbol.getToken(), symbol);
                Match("INCR");
            }
            printTabs();
            Match_Lexeme(";");
        }

        tabs--;

    }

    // Helper Methods
    public Token nextToken() throws IOException, ClassNotFoundException {
        try {
            return (Token) ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }

    public void makeParser() throws IOException, ClassNotFoundException {
        pw = new FileWriter(new File("Tree.txt"));

        currToken = nextToken();
        Start();

        for (String k : symbolTable.keySet()) {
            if (symbolTable.get(k) != null) {
                System.out.println(symbolTable.get(k).toString());
            }
        }

        FileWriter symbolWriter = new FileWriter(new File("SymbolTable.csv"));
        symbolWriter.write("Type,Token,Value\n");
        for (String k : symbolTable.keySet()) {
            if (symbolTable.get(k) != null) {
                symbolWriter.write(symbolTable.get(k).toString());
            }
        }
        symbolWriter.flush();
        symbolWriter.close();
        pw.flush();
        pw.close();
    }

}
