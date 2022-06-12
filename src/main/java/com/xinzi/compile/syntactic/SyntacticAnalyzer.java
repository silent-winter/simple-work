package com.xinzi.compile.syntactic;

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

    private static final Map<String, String> SYNTACTIC_MAP = new LinkedHashMap<>();
    static {
        SYNTACTIC_MAP.put("E", "TX");
        SYNTACTIC_MAP.put("X", "+TX|ε");
        SYNTACTIC_MAP.put("T", "FY");
        SYNTACTIC_MAP.put("Y", "*FY|ε");
        SYNTACTIC_MAP.put("F", "a|(E)");
    }

    public void analyse(List<Pair<String, String>> terminatorList) {
        PredictiveAnalyzer predictiveAnalyzer = new PredictiveAnalyzer(SYNTACTIC_MAP);
        Map<String, Map<String, Set<String>>> predictiveMap = predictiveAnalyzer.getPredictiveMap();
        LinkedList<Character> linkedList = new LinkedList<>();
        linkedList.addLast('E');
        int i = 0;
        while (!linkedList.isEmpty()) {
            Character token = linkedList.getLast();
            linkedList.removeLast();
            Pair<String, String> terminatorPair = i < terminatorList.size() ? terminatorList.get(i) : Pair.of("$", "$");
            if (StringUtils.equals(token.toString(), terminatorPair.getKey())) {
                // 非终结符
                i++;
                continue;
            }
            Map<String, Set<String>> tokenMap = predictiveMap.get(token.toString());
            Set<String> nextTokens = tokenMap.get(terminatorPair.getKey());
            if (nextTokens == null || nextTokens.size() > 1) {
                throw new RuntimeException("error exp");
            }
            String next = nextTokens.stream().findFirst().get();
            if (StringUtils.equals(next, ExpressionUtil.EXPRESSION_EMPTY)) {
                continue;
            }
            for (int j = next.length() - 1; j >= 0; j--) {
                linkedList.addLast(next.charAt(j));
            }
        }
    }

}
