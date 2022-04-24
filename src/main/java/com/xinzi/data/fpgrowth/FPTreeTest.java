package com.xinzi.data.fpgrowth;

import com.xinzi.data.util.SimpleDataUtil;
import javafx.util.Pair;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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
        long start = System.currentTimeMillis();
        FPTree fpTree = new FPTree();
        Map<String, Header> headerTable = fpTree.getHeaderTable();
        List<Pair<PrefixPath, Long>> ans = new ArrayList<>(128);
        headerTable.forEach((k, v) -> {
            Map<PrefixPath, Long> prefixPathMap = fpTree.findPrefixPath(k);
            List<Pair<PrefixPath, Long>> temp = new ArrayList<>(128);
            List<Pair<PrefixPath, Long>> result;
            prefixPathMap.forEach((prefixPath, count) -> temp.add(new Pair<>(prefixPath, count)));
            result = temp.stream().sorted(Comparator.comparingInt(o -> o.getKey().size())).collect(Collectors.toList());
            for (int i = 0; i < result.size(); i++) {
                Pair<PrefixPath, Long> pair1 = result.get(i);
                Long count = pair1.getValue();
                for (int j = i + 1; j < result.size(); j++) {
                    Pair<PrefixPath, Long> pair2 = result.get(j);
                    if (pair1.getKey().size() < pair2.getKey().size() && pair2.getKey().containsAll(pair1.getKey())) {
                        count += pair2.getValue();
                    }
                }
                ans.add(new Pair<>(pair1.getKey(), count));
            }
        });
        Map<Integer, Integer> countMap = new HashMap<>(8);
        ans.stream().filter(e -> e.getValue() >= SimpleDataUtil.SUPPORT)
                .forEach(e -> {
                    int size = e.getKey().size();
                    System.out.println("频繁" + size + "项集: " + e);
                    countMap.putIfAbsent(size, 0);
                    countMap.put(size, countMap.get(size) + 1);
                });
        long end = System.currentTimeMillis();
        AtomicInteger sum = new AtomicInteger();
        countMap.forEach((k, v) -> {
            System.out.println("频繁" + k + "项集个数: " + v);
            sum.addAndGet(v);
        });
        System.out.println("总个数: " + sum.get());
        System.out.println("time: " + (end - start));
    }
}
