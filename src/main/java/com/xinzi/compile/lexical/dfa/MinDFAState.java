package com.xinzi.compile.lexical.dfa;

import com.xinzi.compile.lexical.State;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: xinzi
 * @Date: 2022/04/09/19:26
 */
public class MinDFAState implements State {

    public Set<Integer> dfaIndex;

    public Set<DFAState> dfaStates;

    /**
     * 最小dfa标号
     */
    public int index;

    /**
     * 最小dfa状态转移表
     */
    public Map<Character, Integer> transferMap;

    /**
     * 是否为终结态
     */
    public boolean isEnd;


    public MinDFAState(int index, Set<DFAState> dfaStates) {
        this.index = index;
        this.dfaStates = dfaStates;
        this.dfaIndex = new HashSet<>();
        for (DFAState dfaState : this.dfaStates) {
            this.dfaIndex.add(dfaState.index);
        }
        this.transferMap = new HashMap<>();
        this.dfaStates.forEach(dfaState -> this.isEnd = dfaState.isEnd || this.isEnd);
    }


    public boolean hasDFAState(DFAState dfaState) {
        return this.dfaStates.contains(dfaState);
    }


    /**
     * 接收输入input，转移至下一个最小dfa状态
     * @param dfa
     * @param input
     * @return
     */
    @Override
    public MinDFAState transferTo(DFA dfa, char input) {
        if (!this.transferMap.containsKey(input)) {
            return null;
        }
        Integer targetIndex = this.transferMap.get(input);
        for (MinDFAState minDFAState : dfa.minDFAStateSet) {
            if (minDFAState.index == targetIndex) {
                return minDFAState;
            }
        }
        return null;
    }

    @Override
    public boolean isEnd() {
        return isEnd;
    }
}
