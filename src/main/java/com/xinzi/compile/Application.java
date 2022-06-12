package com.xinzi.compile;

import com.xinzi.compile.syntactic.LrParser;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: xinzi
 * @Date: 2022/06/12/13:57
 */
public class Application {

    public static void main(String[] args) {
        new LrParser().parse();
    }

}
