package com.xinzi.compile.lexical.dfa;

import com.xinzi.compile.lexical.util.OperationUtil;
import com.xinzi.compile.lexical.nfa.NFA;
import com.xinzi.compile.lexical.nfa.NFACouple;
import com.xinzi.compile.lexical.nfa.NFAState;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: xinzi
 * @Date: 2022/04/09/14:15
 */
public class DFA {

    /**
     * dfa状态数
     */
    public int dfaStateCount = 0;

    /**
     * 终结符集合
     */
    public Set<Character> terminators;

    /**
     * dfa所有状态的集合
     */
    public Set<DFAState> dfaStateSet;

    /**
     * dfa终止状态集
     */
    public DFAState startDfa;
    public Set<DFAState> endDfaSet = new HashSet<>();

    /**
     * 最小dfa状态数
     */
    public int minDfaCount = 0;

    /**
     * 最小dfa状态集合
     */
    public Set<MinDFAState> minDFAStateSet;

    /**
     * 最小dfa的终止状态集
     */
    public MinDFAState startMinDfa;
    public Set<MinDFAState> endMinDfaSet = new HashSet<>();


    public DFA(NFA nfa, String postfix) {
        this.terminators = new HashSet<>();
        this.dfaStateSet = new HashSet<>();
        this.minDFAStateSet = new HashSet<>();
        for (int i = 0; i < postfix.length(); i++) {
            char c = postfix.charAt(i);
            if (OperationUtil.isOperator(c)) {
                terminators.add(c);
            }
        }
        create(nfa);
    }


    public DFAState of(Integer index) {
        for (DFAState dfaState : dfaStateSet) {
            if (dfaState.index == index) {
                return dfaState;
            }
        }
        return null;
    }


    private void create(NFA nfa) {
        NFACouple finalNFACouple = nfa.getFinalNFACouple();
        NFAState startState = finalNFACouple.head;
        int finalIndex = finalNFACouple.tail.index;
        LinkedList<DFAState> queue = new LinkedList<>();
        // 初态
        DFAState startDfaState = new DFAState(nfa.emptyClosureStates(startState.index), dfaStateCount);
        startDfaState.isEnd = isEnd(startDfaState.nfaClosureSet, finalIndex);
        if (startDfaState.isEnd) {
            this.endDfaSet.add(startDfaState);
        }
        this.startDfa = startDfaState;
        this.dfaStateCount++;
        dfaStateSet.add(startDfaState);
        queue.addFirst(startDfaState);
        while (!queue.isEmpty()) {
            DFAState currState = queue.getLast();
            queue.removeLast();
            // 遍历所有终结符，求move闭包
            for (Character terminator : terminators) {
                Set<Integer> moveClosure = nfa.moveEmptyClosureStates(currState.nfaClosureSet, terminator);
                DFAState newState = new DFAState(moveClosure, dfaStateCount);
                if (dfaStateSet.contains(newState)) {
                    // 之前已经存在过的状态
                    // 找到该状态，设置转移map
                    for (DFAState dfaState : dfaStateSet) {
                        if (dfaState.equals(newState)) {
                            currState.transferMap.put(terminator, dfaState.index);
                        }
                    }
                    newState = null;

                } else {
                    // 新的dfa状态
                    boolean end = isEnd(moveClosure, finalIndex);
                    if (end) {
                        this.endDfaSet.add(newState);
                    }
                    newState.isEnd = end;
                    this.dfaStateCount++;
                    dfaStateSet.add(newState);
                    queue.addFirst(newState);
                    // 设置转移map
                    currState.transferMap.put(terminator, newState.index);
                }
            }
        }
        // 最小化
        minimize();
    }


    private boolean isEnd(Set<Integer> closure, int terminateIndex) {
        return closure.contains(terminateIndex);
    }


    private void minimize() {
        LinkedList<Set<DFAState>> part = new LinkedList<>();
        LinkedList<Set<DFAState>> copyPart = new LinkedList<>();
        // part[0] 非终结状态，part[1] 终结状态
        part.add(new HashSet<>());
        part.add(new HashSet<>());
        // 初始化集合划分
        for (DFAState dfaState : dfaStateSet) {
            if (dfaState.isEnd) {
                part.get(1).add(dfaState);
            } else {
                part.get(0).add(dfaState);
            }
        }
        while (true) {
            int originSize = part.size();
            for (int i = 0; i < originSize; i++) {
                Set<DFAState> stateSet = part.get(i);
                // 遍历终结符集
                List<Map<Integer, Set<DFAState>>> tempList = new ArrayList<>();
                for (Character terminator : terminators) {
                    // 缓冲区 转移到的集合号 -> 划分的集合中的元素
                    Map<Integer, Set<DFAState>> temp = new HashMap<>();
                    // 遍历每个dfa状态
                    for (DFAState state : stateSet) {
                        if (state.transferMap.containsKey(terminator)) {
                            // 存在转换边
                            Integer target = state.transferMap.get(terminator);
                            DFAState targetState = this.of(target);
                            // 找到转换后的state的划分集合号
                            Integer targetSetIndex = this.findInPartList(part, targetState);
                            temp.putIfAbsent(targetSetIndex, new HashSet<>());
                            temp.get(targetSetIndex).add(state);
                        } else {
                            // 不存在转换边
                            temp.putIfAbsent(-1, new HashSet<>());
                            temp.get(-1).add(state);
                        }
                    }
                    tempList.add(temp);
                }
                // 如果缓冲区中的元素大于1，表示集合需要划分
                int maxSize = 0, maxIndex = 0;
                for (int j = 0; j < tempList.size(); j++) {
                    Map<Integer, Set<DFAState>> temp = tempList.get(j);
                    if (temp.size() > maxSize) {
                        maxSize = temp.size();
                        maxIndex = j;
                    }
                }
                tempList.get(maxIndex).forEach((k, v) -> copyPart.addLast(v));
            }
            if (copyPart.size() == originSize) {
                break;
            } else {
                part.clear();
                part.addAll(copyPart);
                copyPart.clear();
            }
        }
        // 构建最小dfa集合
        for (Set<DFAState> finalStates : copyPart) {
            MinDFAState minDFAState = new MinDFAState(minDfaCount, finalStates);
            DFAState dfaState = finalStates.stream().findFirst().get();
            minDFAState.isEnd = dfaState.isEnd;
            if (dfaState.isEnd) {
                this.endMinDfaSet.add(minDFAState);
            }
            this.minDFAStateSet.add(minDFAState);
            this.minDfaCount++;
        }
        // 设置状态转移
        for (MinDFAState minDFAState : minDFAStateSet) {
            DFAState dfaState = minDFAState.dfaStates.stream().findFirst().get();
            dfaState.transferMap.forEach((terminator, targetDfaIndex) -> {
                int minState = this.findMinDFAState(this.of(targetDfaIndex), minDFAStateSet);
                minDFAState.transferMap.put(terminator, minState);
            });
        }
        // 找到初态
        for (MinDFAState minDFAState : minDFAStateSet) {
            for (DFAState dfaState : minDFAState.dfaStates) {
                if (this.startDfa == dfaState) {
                    this.startMinDfa = minDFAState;
                    return;
                }
            }
        }
    }


    private Integer findInPartList(List<Set<DFAState>> part, DFAState dfaState) {
        for (int i = 0; i < part.size(); i++) {
            Set<DFAState> dfaStateSet = part.get(i);
            if (dfaStateSet.contains(dfaState)) {
                return i;
            }
        }
        return -214124;
    }


    private int findMinDFAState(DFAState dfaState, Set<MinDFAState> minDFAStateSet) {
        for (MinDFAState minDFAState : minDFAStateSet) {
            if (minDFAState.hasDFAState(dfaState)) {
                return minDFAState.index;
            }
        }
        return -1;
    }

}
