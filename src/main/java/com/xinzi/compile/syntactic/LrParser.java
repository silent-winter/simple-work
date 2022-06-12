package com.xinzi.compile.syntactic;

import com.xinzi.compile.lexical.LexicalAnalyzer;
import com.xinzi.compile.lexical.Word;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: xinzi
 * @Date: 2022/06/12/11:19
 */
public class LrParser {

    /**
     * 扫描到的词的列表
     */
    private final List<Word> list;
    /**
     * 当前扫描到的词
     */
    private Word word;
    /**
     * 从列表中获取单词的下标
     */
    private int index = 0;
    /**
     * 用来记录行数
     */
    private int rowNum = 1;
    /**
     * 临时变量标号
     */
    private int tempVariousNum = 1;
    /**
     * 四元式列表
     */
    private final List<List<String>> quaternaryList = new ArrayList<>();
    private int nextQuaternaryIndex = 1;
    private int trueCondition = 1;
    private int falseCondition = 1;

    public LrParser() {
        list = new LexicalAnalyzer().analyse();
        quaternaryList.add(new ArrayList<>());
    }

    public void createAQuaternary(String op, String arg1, String arg2, String result) {
        List<String> quaternary = new ArrayList<>();
        quaternary.add(op);
        quaternary.add(arg1);
        quaternary.add(arg2);
        quaternary.add(result);
        quaternaryList.add(quaternary);
        nextQuaternaryIndex++;
    }

    /**
     * 合并q1和q2，q1,q2是两个四元式在四元式表中的下标
     */
    public int merge(int q1, int q2) {
        int result;
        if (q2 == 0) {
            result = q1;
        } else {
            result = q2;
            backFill(q2, q1);
        }
        return result;
    }

    /**
     * 将result回填到四元式表中下标为qIndex的四元式中的第四个元素中
     */
    public void backFill(int qIndex, int result) {
        if (qIndex > 0) {
            List<String> quaternary = quaternaryList.get(qIndex);
            quaternary.remove(3);
            quaternary.add("" + result);
        }
    }

    /**
     * 分析表达式表达式
     */
    public String expression() {

        String op, arg1, arg2, result;
        arg1 = term();
        result = arg1;

        while(word.getType() == 22 || word.getType() == 23) {
            op = word.getWord();
            word = getNext(list);
            arg2 = term();
            result = newTemp();
            createAQuaternary(op, arg1, arg2, result);
            arg1 = result;
        }
        return result;
    }

    public String term() {
        String op, arg1, arg2, result;
        result = arg1 = factor();
        while(word.getType() == 24 || word.getType() == 25) {
            op = word.getWord();
            word = getNext(list);
            arg2 = factor();
            result = newTemp();
            createAQuaternary(op, arg1, arg2, result);
            arg1 = result;
        }
        return result;
    }

    public boolean match(int typeNum) {
        if (typeNum == word.getType()) {
            word = getNext(list);
            return true;
        }else {
            return false;
        }
    }

    /**
     * "("错误 和 表达式错误
     */
    public String factor() {
        String result;
        if (word.getType() == 10 || word.getType() == 20) {
            result = word.getWord();
            word = getNext(list);
        } else {
            if(!match(26)) {
                System.out.print("'('错误");
                locateError();
                word = getNext(list);
            }
            result = expression();
            if (!match(27)) {
                System.out.print("')'错误");
                locateError();
            }
        }
        return result;
    }

    public void condition() {
        String op, arg1, arg2;
        arg1 = expression();
        if (word.getType() >= 35 && word.getType() <= 40) {
            op = word.getWord();
            word = getNext(list);
            arg2 = expression();
            trueCondition = nextQuaternaryIndex;
            falseCondition = nextQuaternaryIndex + 1;
            op = "goto " + op;
            createAQuaternary(op, arg1, arg2, "0");
            createAQuaternary("goto", "", "", "0");
        } else {
            System.out.print("关系运算符错误");
            locateError();
        }
    }


    public int statement(int chain) {
        String result, arg1;
        int chainTemp = 0;
        int tempFalse;
        switch (word.getType()) {
            case 10:
                result = word.getWord();
                word = getNext(list);
                if(!match(21)) {
                    System.out.print("赋值号错误");
                    locateError();
                }
                arg1 = expression();
                if (!match(34)) {
                    System.out.print("';'错误");
                    locateError();
                }
                createAQuaternary("=", arg1, "", result);
                chain = 0;
                break;
            case 4:
                match(4);
                if(!match(26)) {
                    System.out.print("'('错误");
                    locateError();
                }
                condition();
                backFill(trueCondition, nextQuaternaryIndex);
                if (!match(27)) {
                    System.out.print("')'错误");
                    locateError();
                }

                tempFalse = falseCondition;
                chainTemp = statementBlock(chainTemp);
                chain = merge(chainTemp, tempFalse);
                break;
            case 7:
                match(7);
                int tempIndex = nextQuaternaryIndex;
                if(!match(26)) {
                    System.out.print("'('错误");
                    locateError();
                }
                condition();
                backFill(trueCondition, nextQuaternaryIndex);
                if (!match(27)) {
                    System.out.print("')'错误");
                    locateError();
                }
                tempFalse = falseCondition;
                chainTemp = statementBlock(chainTemp);
                backFill(chainTemp, tempIndex);
                result = "" + tempIndex;
                createAQuaternary("goto", "", "", result);
                chain = tempFalse;
                break;

            default:
                System.out.print("语句错误");
                locateError();
                jumpError();
                break;
        }
        return chain;
    }

    public int statementSequence(int chain) {
        chain = statement(chain);
        while(word.getType() == 10
                ||word.getType() == 4
                ||word.getType() == 7) {
            backFill(chain, nextQuaternaryIndex);
            chain = statement(chain);
        }
        backFill(chain, nextQuaternaryIndex);
        return chain;
    }

    public int statementBlock(int chain) {
        if(!match(30)) {
            System.out.print("'{'错误");
            locateError();
        }
        chain = statementSequence(chain);
        if(!match(31)) {
            System.out.print("'}'错误");
            locateError();
        }
        return chain;
    }

    /**
     * 取下一个词，如果是读取到换行，就增加行数，
     */
    public Word getNext(List<Word> list) {
        if (index < list.size()) {
            Word currentWord = list.get(index++);
            while (currentWord.getType() == 41 || currentWord.getType() == 42) {
                //因为一个换行字符被我转换成长度为2字符串了，所以要除以2
                rowNum += currentWord.getWord().length() / 2;
                currentWord = list.get(index++);
            }
            return currentWord;
        } else {
            return null;
        }
    }

    public void parse() {
        int chain = 0;
        word = getNext(list);
        match(1);
        match(26);
        match(27);
        statementBlock(chain);
        if (word.getType() != 0) {
            System.out.println("程序非正常结束！");
        } else {
            createAQuaternary("halt", "", "", "");
        }
        System.out.println("**************四元式序列*****************");
        printQuaternaryList();
        System.out.println("****************************************");
        System.out.println("*************三元地址代码***************");
        printCode();
        System.out.println("****************************************");

    }

    public void jumpError() {
        while(!";".equals(word.getWord()) && index < list.size()) {
            word = getNext(list);
        }
        if (index < list.size()) {
            word = getNext(list);
        }
    }

    public void locateError() {
        System.out.println(" 识别符号为 " + word.getWord() + " " +  "位置: " + rowNum + "行");
    }

    public void printQuaternaryList() {
        for (int i = 1; i < quaternaryList.size(); i++) {
            List<String > quaternary = quaternaryList.get(i);
            System.out.printf("%3d (",i);
            for (int j = 0; j < 3; j++) {
                System.out.printf("%7s, ", quaternary.get(j));
            }
            System.out.printf("%7s)\n", quaternary.get(3));
        }
    }

    public void printCode() {
        for (int i = 1; i < quaternaryList.size(); i++) {
            List<String > quaternary = quaternaryList.get(i);
            System.out.printf("%3d ",i);
            //操作符为 + - * / < > =
            if (quaternary.get(0).length() == 1) {
                if (quaternary.get(2).trim().length() > 0) {
                    System.out.println(quaternary.get(3) + " = " + quaternary.get(1) + quaternary.get(0) + quaternary.get(2));
                }else {
                    System.out.println(quaternary.get(3) + " " + quaternary.get(0) + " " + quaternary.get(1) + quaternary.get(2));
                }
            }else if (quaternary.get(0).length() == 4) {
                //单纯的goto语句
                System.out.println(quaternary.get(0) + " " + quaternary.get(3));
            }else{
                //带条件的goto
                String[] token = quaternary.get(0).split(" ");
                System.out.println("if (" + quaternary.get(1) + " "
                        + token[1] + " "
                        + quaternary.get(2) + ") "
                        + token[0] + " "
                        + quaternary.get(3));
            }
        }
    }

    public String newTemp() {
        return "t" + tempVariousNum++;
    }

}
