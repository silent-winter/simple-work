package com.xinzi.compile.syntactic;

import com.google.common.collect.Sets;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.xinzi.compile.syntactic.ExpressionUtil.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description: LL(1)预测分析器
 * @Auther: xinzi
 * @Date: 2022/04/29/19:20
 */
@Data
public class PredictiveAnalyzer {

    // first集
    public Map<String, Set<String>> firstMap;
    // follow集
    public Map<String, Set<String>> followMap;
    // select集
    public Map<String, Set<String>> selectMap;
    // 预测分析表
    public Map<String, Map<String, Set<String>>> predictiveMap;

    // 语法表达式
    public Map<String, String> syntacticMap;


    public PredictiveAnalyzer(Map<String, String> syntacticMap) {
        this.firstMap = new HashMap<>();
        this.followMap = new HashMap<>();
        this.selectMap = new HashMap<>();
        this.predictiveMap = new LinkedHashMap<>();
        this.syntacticMap = syntacticMap;
        for (String nonTerminator : this.getAllNonTerminators()) {
            predictiveMap.put(nonTerminator, new HashMap<>());
        }
        initPredictiveAnalyzer();
    }

    private void initPredictiveAnalyzer() {
        this.first();
        this.follow();
        this.select();
        this.predictive();
    }


    private void first() {
        syntacticMap.forEach((left, right) -> {
            this.first(left);
            Set<String> expressionSet = this.spiltByPipe(right);
            for (String expression : expressionSet) {
                this.first(expression);
            }
        });
    }

    private Set<String> first(String symbol) {
        if (firstMap.containsKey(symbol)) {
            return firstMap.get(symbol);
        }
        Set<String> result = new HashSet<>();
        if (symbol.length() == 1) {
            if (isTerminator(symbol.charAt(0)) || isEmptySymbol(symbol.charAt(0))) {
                // 单个终结符或者空串，first集就是自己
                result = Sets.newHashSet(symbol);
                firstMap.put(symbol, result);
                return result;
            }
            // 单个非终结符
            // A-->a...产生式右部以终结符开头，根据定义，这种情况下显然可以看出a属于First(A)
            // A-->B...产生式右部以非终结符开头，此时First(A)需要包括First(B...)
            Set<String> productions = this.spiltByPipe(syntacticMap.get(symbol));
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

    private void follow() {
        Set<String> allNonTerminators = this.getAllNonTerminators();
        Optional<String> startOptional = allNonTerminators.stream().findFirst();
        String start = startOptional.orElseThrow(() -> new RuntimeException("invalid syntacticMap"));
        followMap.putIfAbsent(start, new HashSet<>());
        followMap.get(start).add("$");
        boolean flag = true;
        while (flag) {
            AtomicInteger count = new AtomicInteger(0);
            syntacticMap.forEach((left, right) -> {
                Set<String> expressions = this.spiltByPipe(right);
                for (String expression : expressions) {
                    for (int i = 0; i < expression.length(); i++) {
                        char c = expression.charAt(i);
                        if (isNonTerminator(c)) {
                            String s = String.valueOf(c);
                            followMap.putIfAbsent(s, new HashSet<>());
                            Set<String> set = followMap.get(s);
                            // 如果是非终结符，看他后面一个字符
                            if (i == expression.length() - 1) {
                                // A–>...U 要求的Follow集合的非终结符在产生式结尾
                                // 这时候又要递归推导，U是A的结尾，所以U后面跟随的东西也就是A后面跟随的东西。所以Follow(A)属于Follow(U)。
                                if (!followMap.containsKey(left)) {
                                    count.getAndIncrement();
                                    continue;
                                }
                                Set<String> leftSet = followMap.get(left);
                                if (!set.containsAll(leftSet)) {
                                    count.getAndIncrement();
                                    set.addAll(leftSet);
                                }
                            } else {
                                char next = expression.charAt(i + 1);
                                String nextS = String.valueOf(next);
                                if (isTerminator(next)) {
                                    // 1. A–>...Ua... 要求的Follow集合的非终结符后跟终结符
                                    // 根据定义，显然a属于Follow（U）。这种情况下，Follow（U）和A没有任何关系，产生式左边是什么无所谓。
                                    if (!set.contains(nextS)) {
                                        count.getAndIncrement();
                                        set.add(nextS);
                                    }
                                } else if (isNonTerminator(next)) {
                                    // 2. A–>...UP... 要求的Follow集合的非终结符后跟非终结符
                                    // 根据定义，显然P的第一个符号属于Follow（U），也就是First（P）属于Follow（U）。
                                    Set<String> first = firstMap.get(nextS);
                                    if (!set.containsAll(first)) {
                                        count.getAndIncrement();
                                        set.addAll(first);
                                    }

                                    // 3. A–>...UP并且ε属于First（P）要求的Follow集合的非终结符后跟非结尾的终结符，并且结尾非终结符的First集合包含空串。
                                    // 这是上一种情况的一种特例，除了要按上一种情况处理，First（P）属于Follow（U）以外还要进行分析；
                                    // 因为当P推导为空串时，空串不能出现在Follow集合中，所以U后面跟随的应该是P后面的东西，可P已经是结束的符号，
                                    // 此时U后面显然就是A后面跟随的东西了。所以在这种情况下Follow（A）也属于Follow（U）。
                                    if (i + 1 == expression.length() - 1 && canDeduceEmpty(nextS)) {
                                        if (!followMap.containsKey(left)) {
                                            count.getAndIncrement();
                                            continue;
                                        }
                                        Set<String> leftSet = followMap.get(left);
                                        if (!set.containsAll(leftSet)) {
                                            count.getAndIncrement();
                                            set.addAll(leftSet);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            });
            if (count.get() == 0) {
                flag = false;
            }
        }
        removeAllEmpty();
    }

    private void select() {
        syntacticMap.forEach((left, right) -> {
            Set<String> expressions = this.spiltByPipe(right);
            for (String expression : expressions) {
                String key = left + "->" + expression;
                boolean flag = true;
                selectMap.putIfAbsent(key, new HashSet<>());
                for (int i = 0; i < expression.length(); i++) {
                    char c = expression.charAt(i);
                    if (isTerminator(c)) {
                        flag = false;
                        break;
                    }
                    String s = String.valueOf(c);
                    if (!StringUtils.equals(s, EXPRESSION_EMPTY) && !canDeduceEmpty(s)) {
                        flag = false;
                        break;
                    }
                }
                Set<String> selectSet = selectMap.get(key);
                selectSet.addAll(firstMap.get(expression));
                if (flag) {
                    selectSet.addAll(followMap.get(left));
                    selectSet.remove(EXPRESSION_EMPTY);
                }
            }
        });
    }

    private void predictive() {
        this.selectMap.forEach((key, selectSet) -> {
            String[] split = StringUtils.split(key, "->");
            for (String select : selectSet) {
                Map<String, Set<String>> predict = predictiveMap.get(split[0]);
                predict.putIfAbsent(select, new HashSet<>());
                predict.get(select).add(split[1]);
            }
        });
    }


    // 拿到文法符号串中的所有非终结符
    private Set<String> getAllNonTerminators() {
        return syntacticMap.keySet();
    }

    /**
     * 判断语法推导式右端是否能推导空串
     * @param expression 表达式右端
     */
    private boolean canDeduceEmpty(String expression) {
        if (!syntacticMap.containsKey(expression)) {
            return false;
        }
        Set<String> expressionSet = this.spiltByPipe(syntacticMap.get(expression));
        return expressionSet.contains(EXPRESSION_EMPTY);
    }

    private Set<String> spiltByPipe(String str) {
        return Sets.newHashSet(StringUtils.split(str, "|"));
    }

    private void removeAllEmpty() {
        this.followMap.forEach((k, v) -> v.remove(EXPRESSION_EMPTY));
    }

    public void printFirstMap() {
        this.syntacticMap.forEach((k, v) -> System.out.println(k + "'s FirstSet = " + firstMap.get(k)));
    }

    public void printFollowMap() {
        this.syntacticMap.forEach((k, v) -> System.out.println(k + "'s FollowSet = " + followMap.get(k)));
    }

    public void printSelectMap() {
        this.syntacticMap.forEach((k, v) -> {
            Set<String> set = this.spiltByPipe(v);
            for (String s : set) {
                String key = k + "->" + s;
                System.out.println(key + "'s SelectSet = " + selectMap.get(key));
            }
        });
    }

    public void printPredictiveMap() {
        predictiveMap.forEach((nonTerminator, analyseMap) -> System.out.println(nonTerminator + "'s PredictiveMap = " + analyseMap));
    }

}
