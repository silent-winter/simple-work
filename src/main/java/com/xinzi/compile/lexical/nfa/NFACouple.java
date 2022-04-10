package com.xinzi.compile.lexical.nfa;

/**
 * Created with IntelliJ IDEA.
 * NFA对组，由两个NFA状态构成，一头一尾
 *
 * @Description:
 * @Auther: xinzi
 * @Date: 2022/04/08/23:30
 */
public class NFACouple {

    public NFAState head;

    public NFAState tail;


    public NFACouple() {}

    public NFACouple(int index) {
        NFAState n1 = new NFAState(index);
        NFAState n2 = new NFAState(index + 1);
        this.head = n1;
        this.tail = n2;
    }
}
