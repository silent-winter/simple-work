package com.xinzi.compile.syntactic;

import com.xinzi.compile.lexical.RegKeyEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: xinzi
 * @Date: 2022/04/27/11:33
 */
public class SyntacticAnalyzer {

    /**
     * tiny语言文法
     * E → main()T
     * T → {F}
     * F → G | B | C
     * G → i=A;V
     * V → F | ε
     * A → SA'
     * A'→ +SA' | -SA' | ε
     * S → HS'
     * S' → *H | /H |ε
     * H → (S) | id | number
     * B → if(D)T
     * C → while(D)T
     * D → A>A
     */
    private static final Map<String, String> SYNTACTIC_MAP = new LinkedHashMap<>();
    static {
        SYNTACTIC_MAP.put("E", "m()T");
        SYNTACTIC_MAP.put("T", "{F}");
        SYNTACTIC_MAP.put("F", "G|B|C");
        SYNTACTIC_MAP.put("G", "i=A;V");
        SYNTACTIC_MAP.put("V", "F|ε");
        SYNTACTIC_MAP.put("A", "SZ");
        SYNTACTIC_MAP.put("Z", "+SZ|-SZ|ε");
        SYNTACTIC_MAP.put("S", "HX");
        SYNTACTIC_MAP.put("X", "*H|/H|ε");
        SYNTACTIC_MAP.put("H", "(S)|i|n");
        SYNTACTIC_MAP.put("B", "f(D)TV");
        SYNTACTIC_MAP.put("C", "w(D)TV");
        SYNTACTIC_MAP.put("D", "A>A");
    }

    /**
     * 预测分析器，内含first集、follow集、select集
     */
    private final PredictiveAnalyzer predictiveAnalyzer = new PredictiveAnalyzer(SYNTACTIC_MAP);


    public void analyse(List<Pair<String, String>> terminatorList) {
        Map<String, Map<String, Set<String>>> predictiveMap = predictiveAnalyzer.getPredictiveMap();
        LinkedList<Character> linkedList = new LinkedList<>();
        linkedList.addLast('E');
        int i = 0;
        while (!linkedList.isEmpty()) {
            Character token = linkedList.getLast();
            linkedList.removeLast();
            // key: 正则表达式名称，value: 实际值
            Pair<String, String> terminatorPair = i < terminatorList.size() ? terminatorList.get(i) : Pair.of("$", "$");
            String key = terminatorPair.getKey();
            String value = terminatorPair.getValue();
            RegKeyEnum regKeyEnum = RegKeyEnum.of(key);
            String useToCompare = value;
            if (regKeyEnum == RegKeyEnum.IDENTIFIER || regKeyEnum == RegKeyEnum.NUMBER) {
                useToCompare = key.replaceAll("identifier", "i").replaceAll("number", "n");
            }
            useToCompare = useToCompare.replaceAll("main", "m").replaceAll("while", "w").replaceAll("if", "f");
            if (StringUtils.equals(token.toString(), useToCompare)) {
                // 非终结符
                i++;
                printProcess(linkedList, terminatorList, i, StringUtils.EMPTY);
                continue;
            }
            Map<String, Set<String>> tokenMap = predictiveMap.get(token.toString());
            Set<String> nextTokens = tokenMap.get(useToCompare);
            if (nextTokens == null || nextTokens.size() > 1) {
                System.out.println("语法错误: unexpected value: '" + useToCompare + "', 分析结束");
                throw new RuntimeException("error exp");
            }
            String next = nextTokens.stream().findFirst().get();
            if (StringUtils.equals(next, ExpressionUtil.EXPRESSION_EMPTY)) {
                continue;
            }
            for (int j = next.length() - 1; j >= 0; j--) {
                linkedList.addLast(next.charAt(j));
            }
            printProcess(linkedList, terminatorList, i, token + "->" +next);
        }
        System.out.println("语法分析通过!!");
    }

    private void printProcess(LinkedList<Character> stack, List<Pair<String, String>> input, int begin, String exp) {
        System.out.print("分析栈: " + stack + "\t\t剩余输入: ");
        for (int j = begin; j < input.size(); j++) {
            System.out.print(input.get(j).getValue());
        }
        System.out.println("\t\t产生式: " + exp);
    }

    public void printMidResult() {
        predictiveAnalyzer.printAllResult();
    }

}
