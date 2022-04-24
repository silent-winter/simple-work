package com.xinzi.data.fpgrowth;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: xinzi
 * @Date: 2022/03/10/14:16
 */
@Data
@Builder
public class FPTreeNode {

    public String value;
    public Long count;
    public FPTreeNode nodeLink;
    public FPTreeNode parent;
    public List<FPTreeNode> children;

}
