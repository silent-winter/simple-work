package com.xinzi.data.fpgrowth;

import com.xinzi.data.SimpleDataUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: xinzi
 * @Date: 2022/03/10/14:15
 */
@Data
public class FPTree {

    private final Map<String, Integer> dataMap;
    private final Map<String, Header> headerTable;
    private final FPTreeNode root;


    public FPTree() {
        this.dataMap = SimpleDataUtil.loadDataForFPGrowth();
        this.root = FPTreeNode.builder().value("null").children(new ArrayList<>()).build();
        this.headerTable = new HashMap<>();
        dataMap.forEach((k, v) -> {
            Header header = Header.builder().value(k).count(v).build();
            headerTable.put(k, header);
        });
    }


    public void create() {
        List<List<String>> sortedData = SimpleDataUtil.getSortedData(dataMap);
        for (List<String> records : sortedData) {
            doCreate(root, records);
        }
    }

    public void create(List<List<String>> data) {
        for (List<String> records : data) {
            doCreate(root, records);
        }
    }


    // 查找元素的条件模式基
    public Map<PrefixPath, Integer> findPrefixPath(String key) {
        Map<PrefixPath, Integer> pathMap = new HashMap<>();
        Header header = headerTable.get(key);
        FPTreeNode head = header.head;
        while (head != null) {
            // 从下自上找prefix path
            Integer count = head.count;
            FPTreeNode currNode = head.parent;
            // 自定义path类
            PrefixPath path = new PrefixPath();
            while (currNode.parent != null) {
                path.addFront(currNode.value);
                currNode = currNode.parent;
            }
            pathMap.put(path, count);
            head = head.nodeLink;
        }
        return pathMap;
    }


    private void doCreate(FPTreeNode root, List<String> records) {
        if (records.isEmpty()) {
            return;
        }
        String value = records.get(0);
        List<FPTreeNode> children = root.getChildren();
        int flag = 0, index = 0;
        for (int i = 0; i < children.size(); i++) {
            if (StringUtils.equals(value, children.get(i).getValue())) {
                children.get(i).count += 1;
                flag = 1;
                index = i;
            }
        }
        if (flag == 0) {
            FPTreeNode node = FPTreeNode.builder()
                    .value(value)
                    .count(1)
                    .children(new ArrayList<>())
                    .parent(root).build();
            children.add(node);
            index = children.size() - 1;
            Header header = headerTable.get(value);
            if (header.head == null) {
                header.head = node;
            }
            if (header.tail != null) {
                header.tail.nodeLink = node;
            }
            header.tail = node;
        }
        if (records.size() == 1) {
            return;
        }
        doCreate(children.get(index), records.subList(1, records.size()));
    }

}
