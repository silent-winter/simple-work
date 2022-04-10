package com.xinzi.compile.lexical.nfa;

import com.xinzi.compile.lexical.util.OperationUtil;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: xinzi
 * @Date: 2022/04/09/12:06
 */
public class NFA {

    public List<NFAState> states;

    private final LinkedList<NFACouple> stack;

    public int nfaStateCount = 0;


    public NFA(String postfix) {
        states = new ArrayList<>(64);
        stack = new LinkedList<>();
        create(postfix);
    }


    public NFACouple getFinalNFACouple() {
        return stack.getLast();
    }

    public NFAState of(Integer index) {
        for (NFAState state : this.states) {
            if (state.index == index) {
                return state;
            }
        }
        return null;
    }

    /**
     * 获取nfa状态为index的空转移闭包
     * @return 状态号集合
     */
    public Set<Integer> emptyClosureStates(Integer index) {
        Set<Integer> result = new HashSet<>();
        result.add(index);
        NFAState beginState = this.of(index);
        LinkedList<Integer> queue = new LinkedList<>(beginState.emptyClosureSet);
        while (!queue.isEmpty()) {
            Integer currIndex = queue.getLast();
            NFAState currState = this.of(currIndex);
            queue.removeLast();
            result.add(currIndex);
            for (Integer transferIndex : currState.emptyClosureSet) {
                queue.addFirst(transferIndex);
            }
        }
        return result;
    }

    /**
     * 一个状态集接收终结符value后的空转移闭包
     * @param states 状态集
     * @param value 接收输入的终结符
     * @return 状态号集合
     */
    public Set<Integer> moveEmptyClosureStates(Set<Integer> states, char value) {
        Set<Integer> result = new HashSet<>();
        states.forEach(state -> {
            NFAState nfaState = this.of(state);
            if (nfaState.value == value) {
                result.addAll(this.emptyClosureStates(nfaState.targetIndex));
            }
        });
        return result;
    }


    private void create(String postfix) {
        for (int i = 0; i < postfix.length(); i++) {
            char c = postfix.charAt(i);
            if (OperationUtil.isOperator(c)) {
                // 是操作数
                NFACouple nfaCouple = new NFACouple(nfaStateCount);
                // 入栈
                stack.addLast(nfaCouple);
                // 放入状态数组
                states.add(nfaCouple.head);
                states.add(nfaCouple.tail);
                nfaStateCount += 2;
                // 连接
                add(nfaCouple.head, nfaCouple.tail, c);
            } else {
                if (c == '|') {
                    // nfa栈中弹出两个nfaCouple
                    NFACouple n2 = stack.getLast();
                    stack.removeLast();
                    NFACouple n1 = stack.getLast();
                    stack.removeLast();

                    // 新建nfa并连接
                    NFACouple nfaCouple = new NFACouple(nfaStateCount);
                    states.add(nfaCouple.head);
                    states.add(nfaCouple.tail);
                    nfaStateCount += 2;
                    add(nfaCouple.head, n1.head);
                    add(nfaCouple.head, n2.head);
                    add(n1.tail, nfaCouple.tail);
                    add(n2.tail, nfaCouple.tail);

                    //入栈
                    stack.addLast(nfaCouple);
                } else if (c == '*') {
                    // nfa栈中弹出一个nfaCouple
                    NFACouple n1 = stack.getLast();
                    stack.removeLast();

                    // 新建nfa并连接
                    NFACouple nfaCouple = new NFACouple(nfaStateCount);
                    states.add(nfaCouple.head);
                    states.add(nfaCouple.tail);
                    nfaStateCount += 2;
                    add(n1.tail, nfaCouple.head);
                    add(n1.tail, nfaCouple.tail);
                    add(nfaCouple.head, n1.head);
                    add(nfaCouple.head, nfaCouple.tail);

                    //入栈
                    stack.addLast(nfaCouple);
                } else if (c == '&') {
                    // nfa栈中弹出两个nfaCouple
                    NFACouple n2 = stack.getLast();
                    stack.removeLast();
                    NFACouple n1 = stack.getLast();
                    stack.removeLast();

                    // 连接
                    add(n1.tail, n2.head);

                    // 不需要新建状态，新的couple只需要使用之前的状态
                    NFACouple nfaCouple = new NFACouple();
                    nfaCouple.head = n1.head;
                    nfaCouple.tail = n2.tail;
                    stack.addLast(nfaCouple);
                }
            }
        }
    }

    // 连接两个NFA状态，转移弧为空
    private void add(NFAState n1, NFAState n2) {
        n1.emptyClosureSet.add(n2.index);
    }

    // 连接两个NFA状态，转移弧为value
    private void add(NFAState n1, NFAState n2, char value) {
        n1.targetIndex = n2.index;
        n1.value = value;
    }


    public void print() {
        System.out.println("NFA total states: " + nfaStateCount);
        NFACouple finalNFACouple = getFinalNFACouple();
        System.out.println("初态为: " + finalNFACouple.head.index + ", 终态为: " + finalNFACouple.tail.index);
    }

}
