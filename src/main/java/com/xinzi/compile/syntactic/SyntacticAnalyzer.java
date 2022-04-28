package com.xinzi.compile.syntactic;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: xinzi
 * @Date: 2022/04/27/11:33
 */
public class SyntacticAnalyzer {

    private static final String EXPRESSION_EMPTY = "ε";
    private final Map<String, String> syntacticMap = ImmutableMap.of(
            "E", "TX",
            "X", "+TX|ε",
            "T", "FY",
            "Y", "*FY|ε",
            "F", "a|(E)"
    );
    private final Map<String, String> syntacticMap2 = ImmutableMap.of(
            "S", "AB|bC",
            "A", "b|ε",
            "B", "aD|ε",
            "C", "AD|b",
            "D", "aS|c"
    );
    private final Map<String, Set<String>> firstMap = new HashMap<>();


    public static void main(String[] args) {
        SyntacticAnalyzer syntacticAnalyzer = new SyntacticAnalyzer();
        Set<String> allNonTerminators = syntacticAnalyzer.getAllNonTerminators();
        for (String nonTerminator : allNonTerminators) {
            Set<String> first = syntacticAnalyzer.first(nonTerminator);
            System.out.println(nonTerminator + "'s first set = " + first);
        }
    }


    private Set<String> first(String symbol) {
        if (firstMap.containsKey(symbol)) {
            return firstMap.get(symbol);
        }
        Set<String> result = new HashSet<>();
        if (symbol.length() == 1) {
            if (isTerminator(symbol.charAt(0))) {
                // 单个终结符，first集就是自己
                result = Sets.newHashSet(symbol);
                firstMap.put(symbol, result);
                return result;
            }
            // 单个非终结符
            // A-->a...产生式右部以终结符开头，根据定义，这种情况下显然可以看出a属于First(A)
            // A-->B...产生式右部以非终结符开头，此时First(A)需要包括First(B...)
            Set<String> productions = Sets.newHashSet(StringUtils.split(syntacticMap2.get(symbol), "|"));
            for (String production : productions) {
                if (startWithTerminatorOrEmpty(production)) {
                    result.add(production.substring(0, 1));
                } else {
                    // 递归求first集
                    result.addAll(first(production));
                }
            }
        } else {
            // 多个符号形成的符号串的first集
            String substring = symbol.substring(0, 1);
            result.addAll(first(substring));
            result.remove(EXPRESSION_EMPTY);
            if (canDeduceEmpty(substring)) {
                result.addAll(first(symbol.substring(1)));
            }
        }
        firstMap.put(symbol, result);
        return result;
    }


    // 是否是终结符开头的符号串或者空串
    private boolean startWithTerminatorOrEmpty(String s) {
        return isTerminator(s.charAt(0)) || (s.length() == 1 && isEmptySymbol(s.charAt(0)));
    }

    // 拿到文法符号串中的所有非终结符
    private Set<String> getAllNonTerminators() {
        return syntacticMap2.keySet();
    }

    /**
     * 判断语法推导式右端是否能推导空串
     * @param expression 表达式右端
     */
    private boolean canDeduceEmpty(String expression) {
        Set<String> expressionSet = Sets.newHashSet(StringUtils.split(syntacticMap2.get(expression), "|"));
        return expressionSet.contains(EXPRESSION_EMPTY);
    }

    /**
     * 判断目标字符是否为终结符，约定小写且非空字符表示终结符
     * @param character 输入字符
     */
    private boolean isTerminator(Character character) {
        return !Character.isUpperCase(character) && !isEmptySymbol(character);
    }

    /**
     * 判断是否为空串
     * @param character 输入字符
     */
    private boolean isEmptySymbol(Character character) {
        return StringUtils.equals(EXPRESSION_EMPTY, character.toString());
    }

}
