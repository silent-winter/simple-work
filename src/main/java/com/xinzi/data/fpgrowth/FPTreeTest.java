package com.xinzi.data.fpgrowth;

import com.xinzi.data.util.SimpleDataUtil;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

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
        List<Pair<PrefixPath, Integer>> ans = new ArrayList<>(24);
        headerTable.forEach((k, v) -> {
            Map<PrefixPath, Integer> prefixPathMap = fpTree.findPrefixPath(k);
            List<Pair<PrefixPath, Integer>> temp = new ArrayList<>(24);
            List<Pair<PrefixPath, Integer>> result;
            prefixPathMap.forEach((prefixPath, count) -> temp.add(new Pair<>(prefixPath, count)));
            result = temp.stream().sorted(Comparator.comparingInt(o -> o.getKey().size())).collect(Collectors.toList());
            for (int i = 0; i < result.size(); i++) {
                Pair<PrefixPath, Integer> pair1 = result.get(i);
                int count = pair1.getValue();
                for (int j = i + 1; j < result.size(); j++) {
                    Pair<PrefixPath, Integer> pair2 = result.get(j);
                    if (pair2.getKey().containsAll(pair1.getKey())) {
                        count += pair2.getValue();
                    }
                }
                ans.add(new Pair<>(pair1.getKey(), count));
            }
        });
        ans.stream().filter(e -> e.getValue() >= SimpleDataUtil.SUPPORT)
                .forEach(e -> System.out.println("频繁" + e.getKey().size() + "项集: " + e));
    }
}
