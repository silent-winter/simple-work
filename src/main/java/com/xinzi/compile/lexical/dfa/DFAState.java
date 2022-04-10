package com.xinzi.compile.lexical.dfa;

import com.xinzi.compile.lexical.State;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: xinzi
 * @Date: 2022/04/09/14:09
 */
public class DFAState implements State {

    /**
     * 是否为终态
     */
    public boolean isEnd;

    /**
     * dfa状态号
     */
    public int index;

    /**
     * 状态转移map，接收输入值 -> 转移的目标dfa状态号
     */
    public Map<Character, Integer> transferMap;

    /**
     * nfa的空转移闭包
     */
    public Set<Integer> nfaClosureSet;


    public DFAState(Set<Integer> nfaClosureSet, int index) {
        this.nfaClosureSet = nfaClosureSet;
        this.index = index;
        this.isEnd = false;
        this.transferMap = new HashMap<>();
    }


    @Override
    public int hashCode() {
        int result = 34;
        for (Integer curr : nfaClosureSet) {
            result = result * 17 + curr.hashCode();
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DFAState)) {
            return false;
        }
        DFAState dfaState = (DFAState) obj;
        Set<Integer> targetIntSet = dfaState.nfaClosureSet;
        return nfaClosureSet.containsAll(targetIntSet) && targetIntSet.containsAll(nfaClosureSet);
    }


    @Override
    public DFAState transferTo(DFA dfa, char input) {
        if (!this.transferMap.containsKey(input)) {
            return null;
        }
        Integer target = this.transferMap.get(input);
        for (DFAState dfaState : dfa.dfaStateSet) {
            if (dfaState.index == target) {
                return dfaState;
            }
        }
        return null;
    }

    @Override
    public boolean isEnd() {
        return isEnd;
    }
}
