package com.xinzi.data.fpgrowth;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: xinzi
 * @Date: 2022/03/11/0:21
 */
public class FPTreeTest {

    public static void main(String[] args) {
        FPTree fpTree = new FPTree();
        fpTree.create();
        Map<String, Header> headerTable = fpTree.getHeaderTable();
        headerTable.forEach((k, v) -> {
            Map<PrefixPath, Integer> prefixPathMap = fpTree.findPrefixPath(k);
            System.out.println(k + "==>" + prefixPathMap);
        });
    }

}
