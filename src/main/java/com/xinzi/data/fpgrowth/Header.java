package com.xinzi.data.fpgrowth;

import lombok.Builder;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: xinzi
 * @Date: 2022/03/10/14:51
 */
@Data
@Builder
public class Header {

    public FPTreeNode head;
    public FPTreeNode tail;
    public String value;
    public Long count;

}
