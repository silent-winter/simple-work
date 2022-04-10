package com.xinzi.compile.lexical.util;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: xinzi
 * @Date: 2022/04/08/22:53
 */
public class OperationUtil {

    public static boolean isOperator(char c) {
        return c != '(' && c != ')' && c != '*' && c != '|' && c != '&';
    }


    public static int priority(char c) {
        switch (c) {
            case '*':
                return 3;
            case '&':
                return 2;
            case '|':
                return 1;
            case '(':
                return 0;
            default:
                return -1;
        }
    }


    public static boolean isCalculate(char c) {
        return c == '*' || c == '&' || c == '|';
    }

}
