package com.xinzi.data.util;

import com.xinzi.data.apriori.CandidateSet;

import java.io.File;
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

    // 最小支持度
    public static final Double SUPPORT = 88162 * 0.005;
    // 数据
    public static List<List<String>> SIMPLE_DATA;

    static {
        SIMPLE_DATA = FileUtil.readFile(new File("E:\\JavaEE\\project\\simple-work\\src\\main\\resources\\retail.dat"));
    }


    public static Map<String, Long> loadData() {
        Map<String, Long> dataMap = new HashMap<>();
        for (List<String> simpleDatum : SIMPLE_DATA) {
            for (String key : simpleDatum) {
                if (dataMap.containsKey(key)) {
                    Long count = dataMap.get(key);
                    dataMap.put(key, count + 1);
                } else {
                    dataMap.put(key, 1L);
                }
            }
        }
        // 清理支持度小于SUPPORT的
        clearLessThanSupport(dataMap);
        return dataMap;
    }

    private static void clearLessThanSupport(Map<String, Long> dataMap) {
        Iterator<Map.Entry<String, Long>> iterator = dataMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Long> entry = iterator.next();
            Long count = entry.getValue();
            if (count < SUPPORT) {
                iterator.remove();
            }
        }
    }

    public static List<List<String>> getSortedData(Map<String, Long> dataMap) {
        List<List<String>> nData = new ArrayList<>();
        for (List<String> simpleDatum : SIMPLE_DATA) {
            List<String> sortedList = simpleDatum.stream().filter(dataMap::containsKey).sorted((o1, o2) -> {
                if (Objects.equals(dataMap.get(o1), dataMap.get(o2))) {
                    return o2.compareTo(o1);
                }
                return (int) (dataMap.get(o2) - dataMap.get(o1));
            }).collect(Collectors.toList());
            if (sortedList.isEmpty()) {
                continue;
            }
            nData.add(sortedList);
        }
        return nData;
    }

    public static Long findInOriginItems(CandidateSet candidateSet) {
        Long ans = 0L;
        for (List<String> list : SIMPLE_DATA) {
            if (list.containsAll(candidateSet.data)) {
                ans++;
            }
        }
        return ans;
    }

}
