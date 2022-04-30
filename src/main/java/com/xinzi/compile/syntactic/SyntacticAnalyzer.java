package com.xinzi.compile.syntactic;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: xinzi
 * @Date: 2022/04/27/11:33
 */
public class SyntacticAnalyzer {

    private static final Map<String, String> SYNTACTIC_MAP = new LinkedHashMap<>();
    private static final Map<String, String> SYNTACTIC_MAP2 = new LinkedHashMap<>();
    static {
        SYNTACTIC_MAP2.put("S", "AB|bC");
        SYNTACTIC_MAP2.put("A", "b|ε");
        SYNTACTIC_MAP2.put("B", "aD|ε");
        SYNTACTIC_MAP2.put("C", "AD|b");
        SYNTACTIC_MAP2.put("D", "aS|c");

        SYNTACTIC_MAP.put("E", "TX");
        SYNTACTIC_MAP.put("X", "+TX|ε");
        SYNTACTIC_MAP.put("T", "FY");
        SYNTACTIC_MAP.put("Y", "*FY|ε");
        SYNTACTIC_MAP.put("F", "a|(E)");
    }


    public static void main(String[] args) {
        PredictiveAnalyzer predictiveAnalyzer = new PredictiveAnalyzer(SYNTACTIC_MAP2);
        predictiveAnalyzer.printFirstMap();
        System.out.println();
        predictiveAnalyzer.printFollowMap();
        System.out.println();
        predictiveAnalyzer.printSelectMap();
        System.out.println();
        predictiveAnalyzer.printPredictiveMap();
    }

}
