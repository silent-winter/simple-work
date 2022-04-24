package com.xinzi.data.apriori;

import com.xinzi.data.util.SimpleDataUtil;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: xinzi
 * @Date: 2022/03/11/16:59
 */
public class AprioriSolution {

    private final Map<String, Long> originData;

    private Map<CandidateSet, Long> data;


    public AprioriSolution() {
        this.originData = SimpleDataUtil.loadData();
        this.data = new HashMap<>();
        originData.forEach((k, v) -> {
            CandidateSet candidateSet = new CandidateSet(k);
            data.put(candidateSet, v);
        });
        pruning();
    }

    private void pruning() {
        Integer support = SimpleDataUtil.SUPPORT;
        int size = this.itemSize();
        System.out.print("频繁" + size + "项集: ");
        Iterator<Map.Entry<CandidateSet, Long>> iterator = data.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<CandidateSet, Long> entry = iterator.next();
            Long count = entry.getValue();
            if (count < support) {
                iterator.remove();
            }
        }
        System.out.println(data);
        System.out.println("频繁" + size + "项集个数: " + data.size());
    }

    public int itemSize() {
        return data.size() == 0 ? 0 : data.keySet().stream().findFirst().get().size();
    }

    public void extend(int targetItemSize) {
        if (targetItemSize - this.itemSize() != 1) {
            throw new RuntimeException("error in extend, invalid param!!");
        }
        Set<String> elements = originData.keySet();
        Map<CandidateSet, Long> newData = new HashMap<>();
        data.forEach((candidateSet, support) -> {
            for (String element : elements) {
                candidateSet.add(element);
                if (candidateSet.size() == targetItemSize) {
                    Long count = SimpleDataUtil.findInOriginItems(candidateSet);
                    newData.put(new CandidateSet(candidateSet), count);
                }
                candidateSet.remove(element);
            }
        });
        data.clear();
        data = newData;
        pruning();
    }

}
