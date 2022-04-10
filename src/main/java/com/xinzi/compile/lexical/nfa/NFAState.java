package com.xinzi.compile.lexical.nfa;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: xinzi
 * @Date: 2022/04/08/23:27
 */
public class NFAState {

    /**
     * NFA状态号
     */
    public int index;

    /**
     * 接收的弧边非空输入
     */
    public char value;

    /**
     * 接收弧边非空输入，转移到的目标状态号
     */
    public int targetIndex;

    /**
     * 接收空输入，转移到的状态号集合
     */
    public Set<Integer> emptyClosureSet;


    public NFAState(int index) {
        this.index = index;
        // 表示空值
        this.value = '#';
        this.targetIndex = -1;
        this.emptyClosureSet = new HashSet<>();
    }

}
