package com.xinzi.data.apriori;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: xinzi
 * @Date: 2022/04/10/18:59
 */
public class AprioriTest {

    public static void main(String[] args) {
        AprioriSolution aprioriSolution = new AprioriSolution();
        int start = 2;
        while (aprioriSolution.itemSize() > 0) {
            aprioriSolution.extend(start);
            start++;
        }
    }

}
