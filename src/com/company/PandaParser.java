package com.company;

import java.io.*;
import java.util.HashMap;
import java.util.Objects;


public class PandaParser {

    // Filing Variables
    private static ObjectInputStream ois;
    private static ObjectOutputStream oos3AC;

    private int forLastELIF = 0;

    private static FileWriter pw;

    //Keeps Count of tabs for parse Trees
    private static int tabs;

    //Keeps count of line number for 3 Address Code
    private static int lineCounter;

    //Keeps count of temp number
    private static int tempNumber;

    //Keeps Track of the current Token for parser
    private static Token currToken;

    //Symbol variable for Symbol Table
    private Symbol temp;

    //Hash Maps
    HashMap<String, Symbol> symbolTable;
    HashMap<Integer, String> _3AddressCode;

    //BackPatching Flags
    private Boolean end_line_flag = Boolean.FALSE;


    public PandaParser(String filename) throws IOException {
        File readFile = new File(filename + ".out");
        ois = new ObjectInputStream(new FileInputStream(readFile));

        File writeFile3AC = new File("ThreeAddressCode.out");
        oos3AC = new ObjectOutputStream(new FileOutputStream(writeFile3AC));

        temp = new Symbol();
        lineCounter = 0;
        tabs = 0;
        tempNumber = 0;

        symbolTable = new HashMap<String, Symbol>();
        _3AddressCode = new HashMap<Integer, String>();
    }

    public void emit(String line) {
        _3AddressCode.put(lineCounter++, line);
    }

    public void backPatch(int lineNum, int line) {
        String existing = _3AddressCode.get(lineNum);
        existing += String.valueOf(line);
        _3AddressCode.put(lineNum, existing);
    }

    public String newTemp() {

        return "T" + String.valueOf(++tempNumber);
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
    private String isLITERAL() throws IOException, ClassNotFoundException {
        tabs++;
        String temp = null;
        if (currToken.getName().equals("NUM") || currToken.getName().equals("STRING") || currToken.getName().equals("CHARL")) {
            printTabs();
            pw.write(currToken.getName() + " ( " + currToken.getLexeme() + ")\n");
            temp = currToken.getLexeme();
            currToken = nextToken();
        } else if (currToken.getName().equals("SUB")) {
            currToken = nextToken();
            if (currToken.getName().equals("NUM")) {
                printTabs();
                pw.write(currToken.getName() + " ( -" + currToken.getLexeme() + ")\n");
                temp = "-" + currToken.getLexeme();
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
        return temp;
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
                    //int AOv = AO();
                    AOHelper tempAO = AO();
                    int AOv = tempAO.get_intvalue();
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
            String iopInp = currToken.getLexeme();
            Match("ID");
            printTabs();
            System.out.println("IOP()");
            pw.write("IOP()\n");
            IOP(iopInp);
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
            String copInp = currToken.getLexeme();
            Match("ID");
            printTabs();
            System.out.println("COP()");
            pw.write("COP()\n");
            COP(copInp);
            printTabs();
            Match_Lexeme(";");

        }

        tabs--;
    }

    private void IOP(String inp) throws IOException, ClassNotFoundException {
        tabs++;

        if (currToken.isLexEqual("=")) {
            printTabs();
            Match_Lexeme("=");
            printTabs();
            System.out.println("AO()");
            pw.write("AO()\n");
            //int AOv = AO();
            AOHelper tempAO = AO();
            emit(inp + " = " + tempAO.get_3addressValue());
            int AOv = tempAO.get_intvalue();
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
            String iopInp = currToken.getLexeme();
            Match("ID");
            printTabs();
            System.out.println("IOP()");
            pw.write("IOP()\n");
            IOP(iopInp);
        }

        tabs--;
    }

    private void COP(String inp) throws IOException, ClassNotFoundException {
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
                emit(currToken.getLexeme() + " = " + inp);
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
            String copInp = currToken.getLexeme();
            Match("ID");
            printTabs();
            System.out.println("COP()");
            pw.write("COP()\n");
            COP(copInp);

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
        _3AddressCode.put(lineCounter++, "in " + currToken.getLexeme());

        Match("ID");


        tabs--;
    }

    private void P() throws IOException, ClassNotFoundException {
        tabs++;

        if (currToken.isEqual("PRINT")) {
            printTabs();
            Match("PRINT");
            _3AddressCode.put(lineCounter, "out ");
        } else if (currToken.isEqual("PRINTLN")) {
            printTabs();
            Match("PRINTLN");
            _3AddressCode.put(lineCounter, "out ");
            end_line_flag = Boolean.TRUE;
        } else {
            System.out.println("BAD TOKEN");
            System.exit(3);
        }

        tabs--;
    }

    private void POP() throws IOException, ClassNotFoundException {
        tabs++;
        String s = _3AddressCode.get(lineCounter);
        if (currToken.isEqual("ID")) {
            printTabs();
            _3AddressCode.put(lineCounter++, s + currToken.getLexeme());
            Match("ID");
        } else if (isLITERALBOOL()) {
            printTabs();
            System.out.println("LITERAL()");
            pw.write("LITERAL()\n");
            _3AddressCode.put(lineCounter++, s + "'" + currToken.getLexeme() + "'");
            isLITERAL();
        } else {
            printTabs();
            System.out.println("AO()");
            pw.write("AO()\n");
            //int val = AO();
            AOHelper tempAO = AO();
            int val = tempAO.get_intvalue();
            _3AddressCode.put(lineCounter++, s + String.valueOf(val));
        }
        if (end_line_flag == Boolean.TRUE) {
            _3AddressCode.put(lineCounter++, "out end-line");
            end_line_flag = Boolean.FALSE;
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
            String type = CK();
            printTabs();
            System.out.println("EXP()");
            pw.write("EXP()\n");
            int[] be = EXP(type);
            backPatch(be[0], lineCounter);
            printTabs();
            Match_Lexeme(":");
            printTabs();
            Match_Lexeme("{");
            printTabs();
            System.out.println("ST()");
            pw.write("ST()\n");
            ST();
            int stnext = -1;
            if (Objects.equals(type, "If")) {
                stnext = lineCounter;
                // emit("goto ");
            } else if (Objects.equals(type, "While")) {
                emit("goto  " + be[0]);
                backPatch(be[1], lineCounter);
            }
            printTabs();
            Match_Lexeme("}");
            printTabs();
            System.out.println("ES()");
            pw.write("ES()\n");
            String options = ES(stnext, be[1]);
            if (Objects.equals(type, "If")) {
                if (options == null) {
                    //no else & no elif
                    backPatch(be[1], stnext);
                    backPatch(stnext, stnext + 1);
                }
            }
        }
        tabs--;
    }

    private String CK() throws IOException, ClassNotFoundException {
        tabs++;
        String temp = null;
        if (currToken.isEqual("WHILE")) {
            printTabs();
            temp = "While";
            Match("WHILE");
        } else if (currToken.isEqual("IF")) {
            printTabs();
            temp = "If";
            Match("IF");
        }

        tabs--;
        return temp;
    }

    private int[] EXP(String type) throws IOException, ClassNotFoundException {
        tabs++;

        printTabs();
        System.out.println("EV()");
        pw.write("EV()\n");
        String id1 = EV();
        printTabs();
        String ro = currToken.getLexeme();
        Match("RO");
        printTabs();
        System.out.println("EV()");
        pw.write("EV()\n");
        String id2 = EV();

        int[] retObj = new int[2];
        retObj[0] = lineCounter;
        emit(type + " " + id1 + " " + ro + " " + id2 + " goto ");
        retObj[1] = lineCounter;
        emit("goto ");

        tabs--;
        return retObj;
    }

    private String EV() throws IOException, ClassNotFoundException {
        tabs++;

        String temp = null;
        if (currToken.isEqual("ID")) {
            printTabs();
            temp = currToken.getLexeme();
            Match("ID");
        } else {
            printTabs();
            System.out.println("LITERAL()");
            pw.write("LITERAL()\n");
            temp = isLITERAL();
        }

        tabs--;
        return temp;
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
        } else if (currToken.isEqual("WHILE") || currToken.isEqual("IF")) {
            printTabs();
            System.out.println("AS()");
            pw.write("AS()\n");
            CS();
            printTabs();
            System.out.println("ST()");
            pw.write("ST()\n");
            ST();
        }

        tabs--;
    }

    private String ES(int next, int BEf) throws IOException, ClassNotFoundException {
        tabs++;
        String temp = null;
        AOHelper helper = null;
        if (currToken != null) {
            if (currToken.isEqual("ELIF")) {
                printTabs();
                Match("ELIF");
                backPatch(BEf, lineCounter);
                temp = "Elif";
                printTabs();
                System.out.println("EXP()");
                pw.write("EXP()\n");
                int[] bp = EXP("Elif");
                backPatch(bp[0], lineCounter);
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
                ES(next, bp[1]);
            } else if (currToken.isEqual("ELSE")) {
                printTabs();
                Match("ELSE");
                temp = "Else";
                printTabs();
                Match_Lexeme(":");
                printTabs();
                Match_Lexeme("{");
                printTabs();
                System.out.println("ST()");
                pw.write("ST()\n");
                next = lineCounter;
                emit("goto ");
                backPatch(BEf, lineCounter);
                ST();
                backPatch(next, lineCounter);
                printTabs();
                Match_Lexeme("}");
            } else {
                backPatch(BEf, lineCounter);
            }
        }


        //backPatch(BEf, lineCounter);
        tabs--;
        return temp;
    }

    //CONTROL STATEMENTS START


    //ARTHEMETIC STATEMENTS START

    private AOHelper AO() throws IOException, ClassNotFoundException {
        tabs++;

        printTabs();
        System.out.println("T()");
        pw.write("T()\n");
        AOHelper Tv = T();
        printTabs();
        System.out.println("EDASH()");
        pw.write("EDASH()\n");
        AOHelper AOv = EDASH(Tv);

        tabs--;

        return AOv;
    }

    private AOHelper EDASH(AOHelper EdashI) throws IOException, ClassNotFoundException {
        tabs++;


        if (currToken.isEqual("ADD")) {
            printTabs();
            Match("ADD");
            printTabs();
            System.out.println("T()");
            pw.write("T()\n");
            //int Tv = T();
            AOHelper Tv = T();
            EdashI._intvalue += Tv._intvalue;
            String temp3AV = EdashI.get_3addressValue();
            EdashI._3addressValue = newTemp();
            emit(EdashI.get_3addressValue() + " = " + temp3AV + " + " + Tv.get_3addressValue());
            printTabs();
            System.out.println("EDASH()");
            pw.write("EDASH()\n");
            AOHelper EdashS = EDASH(EdashI);

            tabs--;
            return EdashS;

        } else if (currToken.isEqual("SUB")) {
            printTabs();
            Match("SUB");
            printTabs();
            System.out.println("T()");
            pw.write("T()\n");
            AOHelper Tv = T();
            EdashI._intvalue -= Tv._intvalue;
            String temp3AV = EdashI.get_3addressValue();
            EdashI._3addressValue = newTemp();
            emit(EdashI.get_3addressValue() + " = " + temp3AV + " - " + Tv.get_3addressValue());
            printTabs();
            System.out.println("EDASH()");
            pw.write("EDASH()\n");
            AOHelper EdashS = EDASH(EdashI);

            tabs--;
            return EdashS;

        } else {
            tabs--;
            return EdashI;
        }

    }

    private AOHelper T() throws IOException, ClassNotFoundException {
        tabs++;

        printTabs();
        System.out.println("F()");
        pw.write("F()\n");
        //int Fv = F();
        AOHelper Fv = F();
        printTabs();
        System.out.println("TDASH()");
        pw.write("TDASH()\n");

        AOHelper TdashS = TDASH(Fv);

        tabs--;
        return TdashS;


    }

    private AOHelper TDASH(AOHelper TdashI) throws IOException, ClassNotFoundException {
        tabs++;

        if (currToken.isEqual("MUL")) {
            printTabs();
            Match("MUL");
            printTabs();
            System.out.println("F()");
            pw.write("F()\n");
            //int Fv = F();
            AOHelper Fv = F();
            TdashI._intvalue *= Fv._intvalue;
            String temp3AV = TdashI.get_3addressValue();
            TdashI.set_3addressValue(newTemp());
            emit(TdashI.get_3addressValue() + " = " + temp3AV + " * " + Fv._3addressValue);
            printTabs();
            System.out.println("TDASH()");
            pw.write("TDASH()\n");
            AOHelper TdashS = TDASH(TdashI);

            tabs--;
            return TdashS;
        } else if (currToken.isEqual("DIV")) {
            printTabs();
            Match("DIV");
            printTabs();
            System.out.println("F()");
            pw.write("F()\n");
            AOHelper Fv = F();
            TdashI._intvalue /= Fv._intvalue;
            String temp3AV = TdashI.get_3addressValue();
            TdashI.set_3addressValue(newTemp());
            emit(TdashI.get_3addressValue() + " = " + temp3AV + " / " + Fv._3addressValue);
            printTabs();
            System.out.println("TDASH()");
            pw.write("TDASH()\n");
            AOHelper TdashS = TDASH(TdashI);

            tabs--;
            return TdashS;
        } else {
            tabs--;
            return TdashI;
        }


    }

    private AOHelper F() throws IOException, ClassNotFoundException {
        tabs++;

        if (currToken.isEqual("ID")) {
            printTabs();
            Symbol symbol = symbolTable.get(currToken.getLexeme());
            AOHelper FinalV = new AOHelper();
            if (symbol != null) {
                FinalV.set_intvalue(Integer.parseInt(symbol.getValue()));
                FinalV.set_3addressValue(currToken.getLexeme());
            } else {
                System.out.println("Variable accessed before Declaration...");
                System.exit(4);
            }
            Match("ID");

            tabs--;
            return FinalV;
        } else if (currToken.isEqual("NUM")) {
            printTabs();
            AOHelper FinalV = new AOHelper();
            FinalV.set_intvalue(Integer.parseInt(currToken.getLexeme()));
            FinalV.set_3addressValue(currToken.getLexeme());
            Match("NUM");

            tabs--;
            return FinalV;
        } else if (currToken.getName().equals("SUB")) {
            printTabs();
            Match("SUB");
            if (currToken.getName().equals("NUM")) {
                AOHelper FinalV = new AOHelper();
                FinalV.set_intvalue(Integer.parseInt(currToken.getLexeme()));
                FinalV.set_3addressValue("-" + currToken.getLexeme());
                Match("NUM");

                tabs--;
                return FinalV;
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
            AOHelper AOv = AO();
            printTabs();
            Match_Lexeme(")");

            tabs--;
            return AOv;


        } else {
            System.out.println("BAD TOKEN");
            System.exit(3);
        }
        return null;
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
            String ASv = currToken.getLexeme();
            Match("ID");
            if (currToken.isLexEqual("=")) {
                printTabs();
                Match_Lexeme("=");

                if (symbol.getType().equals("CHAR")) {
                    if (currToken.isEqual("CHARL")) {
                        printTabs();
                        symbol.setValue(currToken.getLexeme());
                        symbolTable.replace(symbol.getToken(), symbol);
                        emit(ASv + " = " + currToken.getLexeme());
                        Match("CHARL");
                    } else if (currToken.isEqual("STRING")) {
                        printTabs();
                        emit(ASv + " = " + currToken.getLexeme());
                        Match("STRING");
                    } else if (currToken.isEqual("ID")) {
                        Symbol rhs = symbolTable.get(currToken.getLexeme());
                        if (rhs == null) {
                            System.out.println("Assignment before declaration..");
                            System.exit(4);
                        }
                        symbol.setValue(rhs.getValue());
                        symbolTable.replace(symbol.getToken(), symbol);
                        emit(ASv + " = " + currToken.getLexeme());
                        Match("ID");
                    }
                } else {
                    printTabs();
                    System.out.println("AO()");
                    pw.write("AO()\n");
                    //int AOv = AO();
                    AOHelper temp = AO();
                    int AOv = temp.get_intvalue();
                    symbol.setValue(String.valueOf(AOv));
                    symbolTable.replace(symbol.getToken(), symbol);
                    emit(ASv + " = " + temp.get_3addressValue());
                }

            } else if (currToken.isEqual("INCR")) {
                printTabs();
                symbol.setValue(String.valueOf(Integer.parseInt(symbol.getValue()) + 1));
                symbolTable.replace(symbol.getToken(), symbol);
                emit(ASv + " = " + ASv + " + 1");
                Match("INCR");
            }
            printTabs();
            Match_Lexeme(";");
        }

        tabs--;

    }

    // Helper Methods
    public Token nextToken() throws IOException {
        try {
            return (Token) ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }

    public void makeParser() throws IOException, ClassNotFoundException {
        pw = new FileWriter("Tree.txt");

        //Parser Starts here -->
        currToken = nextToken();
        Start();

        // Populate Symbol Table in CSV
        FileWriter symbolWriter = new FileWriter(new File("SymbolTable.csv"));
        // CSV Headers
        symbolWriter.write("Type,Token,Value\n");

        // Writing all symbols
        for (String k : symbolTable.keySet()) {
            if (symbolTable.get(k) != null) {
                symbolWriter.write(symbolTable.get(k).toString());
            }
        }

        //Writing 3 address code on screen and on File
        System.out.println("\n------------ ------------ \n3 Address Code Output  \n------------ ------------ ");
        for (int i = 0; i < _3AddressCode.size(); i++) {
            System.out.println((i) + ": " + _3AddressCode.get(i));
            oos3AC.writeObject(new ThreeAddressCode(i, _3AddressCode.get(i)));
        }

        PandaVM pandaVM = new PandaVM(_3AddressCode, symbolTable);
        pandaVM.run();

        symbolWriter.flush();
        symbolWriter.close();
        pw.flush();
        pw.close();
    }

}
