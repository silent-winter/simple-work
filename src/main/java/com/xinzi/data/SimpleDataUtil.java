package com.xinzi.data;

import com.xinzi.data.apriori.CandidateSet;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: xinzi
 * @Date: 2022/03/10/14:29
 */
public class SimpleDataUtil {

    // 数据
    public static final List<List<String>> SIMPLE_DATA = new ArrayList<List<String>>(){{
        add(Arrays.asList("r", "z", "h", "j", "p"));
        add(Arrays.asList("z", "y", "x", "w", "v", "u", "t", "s"));
        add(Collections.singletonList("z"));
        add(Arrays.asList("r", "x", "n", "o", "s"));
        add(Arrays.asList("y", "r", "x", "z", "q", "t", "p"));
        add(Arrays.asList("y", "z", "x", "e", "q", "s", "t", "m"));
    }};
    // 最小支持度
    public static final Integer SUPPORT = 2;


    public static Map<String, Integer> loadDataForFPGrowth() {
        Map<String, Integer> dataMap = loadDataForApriori();
        // 清理支持度小于SUPPORT的
        clearLessThanSupport(dataMap);
        return dataMap;
    }


    private static void clearLessThanSupport(Map<String, Integer> dataMap) {
        Iterator<Map.Entry<String, Integer>> iterator = dataMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            Integer count = entry.getValue();
            if (count < SUPPORT) {
                iterator.remove();
            }
        }
    }


    public static Map<String, Integer> loadDataForApriori() {
        Map<String, Integer> dataMap = new HashMap<>();
        for (List<String> simpleDatum : SIMPLE_DATA) {
            for (String key : simpleDatum) {
                if (dataMap.containsKey(key)) {
                    Integer count = dataMap.get(key);
                    dataMap.put(key, count + 1);
                } else {
                    dataMap.put(key, 1);
                }
            }
        }
        return dataMap;
    }


    public static List<List<String>> getSortedData(Map<String, Integer> dataMap) {
        List<List<String>> nData = new ArrayList<>();
        for (List<String> simpleDatum : SIMPLE_DATA) {
            List<String> sortedList = simpleDatum.stream().filter(dataMap::containsKey).sorted((o1, o2) -> {
                if (Objects.equals(dataMap.get(o1), dataMap.get(o2))) {
                    return o2.compareTo(o1);
                }
                return dataMap.get(o2) - dataMap.get(o1);
            }).collect(Collectors.toList());
            nData.add(sortedList);
        }
        System.out.println(nData);
        return nData;
    }


    public static int findInOriginItems(CandidateSet candidateSet) {
        int ans = 0;
        for (List<String> list : SIMPLE_DATA) {
            if (list.containsAll(candidateSet.data)) {
                ans++;
            }
        }
        return ans;
    }

}
