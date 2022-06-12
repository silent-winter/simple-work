package com.xinzi.compile.lexical;

import com.xinzi.compile.lexical.dfa.DFA;
import com.xinzi.compile.lexical.nfa.NFA;
import com.xinzi.compile.lexical.util.FileUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.xinzi.compile.lexical.util.OperationUtil.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: xinzi
 * @Date: 2022/04/08/21:59
 */
public class LexicalAnalyzer {

    /**
     * 词法分析核心函数
     */
    public List<Word> analyse() {
        List<Word> result = new ArrayList<>();
        // 读取程序文件
        String programme = FileUtil.readFile(new File("E:\\JavaEE\\project\\simple-work\\src\\main\\resources\\source_char.txt")).trim();
        // 读取正则表达式配置文件
        Map<String, String> regexpMap = FileUtil.readProperties("grammer");

        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < programme.length(); i++) {
            char c = programme.charAt(i);
            if (c == '\n' || c == '\r' || c == ' ') {
                if (StringUtils.isBlank(temp)) {
                    continue;
                }
                String word = temp.toString();
                if (StringUtils.equals(temp, "(") || StringUtils.equals(temp, ")")) {
                    result.add(new Word(Word.typeOf("brackets", word), word));
                } else {
                    for (String key : regexpMap.keySet()) {
                        // 自动机逐个分析
                        boolean analyze = this.analyze(regexpMap.get(key), word, true);
                        if (analyze) {
                            result.add(new Word(Word.typeOf(key, word), word));
                            break;
                        }
                    }
                }
                if (c != ' ') {
                    result.add(new Word(41, "HH"));
                }
                temp = new StringBuilder();
            } else {
                temp.append(c);
            }
        }
        // 分析最后一个词
        if (StringUtils.isNoneBlank(temp)) {
            for (String key : regexpMap.keySet()) {
                // 自动机逐个分析
                String word = temp.toString();
                boolean analyze = this.analyze(regexpMap.get(key), temp.toString(), true);
                if (analyze) {
                    result.add(new Word(Word.typeOf(key, word), word));
                    break;
                }
            }
        }
        return result;
    }

    public boolean analyze(String regexp, String input, boolean useMinDfa) {
        DFA dfa = this.toDFA(regexp);
        State currState = useMinDfa ? dfa.startMinDfa : dfa.startDfa;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            currState = currState.transferTo(dfa, c);
            if (currState == null) {
                return false;
            }
        }
        return currState.isEnd();
    }

    private DFA toDFA(String regexp) {
        String infix = init(regexp);
        String postfix = infixToPostfix(infix);
        NFA nfa = new NFA(postfix);
        return new DFA(nfa, postfix);
    }

    private String init(String exp) {
        int offset = 1;
        StringBuilder result = new StringBuilder(exp);
        for (int i = 0; i < exp.length() - 1; i++) {
            char c1 = exp.charAt(i);
            char c2 = exp.charAt(i + 1);
            if ((isOperator(c1) || c1 == ')' || c1 == '*') && (isOperator(c2) || c2 == '(')) {
                result.insert(i + offset, '&');
                offset++;
            }
        }
        return result.toString();
    }

    private String infixToPostfix(String infix) {
        StringBuilder res = new StringBuilder();
        LinkedList<Character> stack = new LinkedList<>();
        for (int i = 0; i < infix.length(); i++) {
            char c = infix.charAt(i);
            if (isOperator(c)) {
                // 是操作数
                res.append(c);
            } else {
                if (c == '(') {
                    stack.addLast(c);
                } else if (c == ')') {
                    while (true) {
                        Character last = stack.getLast();
                        stack.removeLast();
                        if (last == '(') {
                            break;
                        }
                        res.append(last);
                    }
                } else {
                    // 直到在栈中找到优先级比当前运算符优先级小的，结束循环
                    int priority = priority(c);
                    while (!stack.isEmpty()) {
                        Character last = stack.getLast();
                        if (isOperator(last)) {
                            res.append(last);
                            stack.removeLast();
                            continue;
                        }
                        int currPriority = priority(last);
                        if (currPriority < priority) {
                            break;
                        }
                        res.append(last);
                        stack.removeLast();
                    }
                    stack.addLast(c);
                }
            }
        }
        // 清空栈中元素
        while (!stack.isEmpty()) {
            res.append(stack.getLast());
            stack.removeLast();
        }
        return res.toString();
    }
}
