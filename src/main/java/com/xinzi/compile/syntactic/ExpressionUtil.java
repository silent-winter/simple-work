package com.xinzi.compile.syntactic;

import org.apache.commons.lang3.StringUtils;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: xinzi
 * @Date: 2022/04/29/19:23
 */
public class ExpressionUtil {

    public static final String EXPRESSION_EMPTY = "ε";


    // 是否是终结符开头的符号串或者空串
    public static boolean startWithTerminatorOrEmpty(String s) {
        return isTerminator(s.charAt(0)) || (s.length() == 1 && isEmptySymbol(s.charAt(0)));
    }

    /**
     * 判断目标字符是否为终结符，约定小写且非空字符表示终结符
     * @param character 输入字符
     */
    public static boolean isTerminator(Character character) {
        return !Character.isUpperCase(character) && !isEmptySymbol(character);
    }

    public static boolean isNonTerminator(Character character) {
        return Character.isUpperCase(character);
    }

    /**
     * 判断是否为空串
     * @param character 输入字符
     */
    public static boolean isEmptySymbol(Character character) {
        return StringUtils.equals(EXPRESSION_EMPTY, character.toString());
    }

}
