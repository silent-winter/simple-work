package com.xinzi.compile.lexical;

import com.xinzi.compile.lexical.dfa.DFA;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: xinzi
 * @Date: 2022/04/09/23:02
 */
public interface State {

    State transferTo(DFA dfa, char input);

    boolean isEnd();

}
