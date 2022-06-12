package com.xinzi.compile;

import com.xinzi.compile.lexical.LexicalAnalyzer;
import com.xinzi.compile.syntactic.SyntacticAnalyzer;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: xinzi
 * @Date: 2022/06/12/13:57
 */
public class Application {

    public static void main(String[] args) {
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer();
        SyntacticAnalyzer syntacticAnalyzer = new SyntacticAnalyzer();
        System.out.println("=====================词法分析结果=====================");
        List<Pair<String, String>> tokenList = lexicalAnalyzer.analyse();
        syntacticAnalyzer.printMidResult();
        System.out.println("=====================语法分析过程=====================");
        syntacticAnalyzer.analyse(tokenList);
    }

}
