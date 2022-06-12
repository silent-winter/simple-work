package com.xinzi.compile.lexical;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: xinzi
 * @Date: 2022/06/12/12:04
 */
@Getter
@AllArgsConstructor
public enum RegKeyEnum {

    KEYWORD("keyword"),
    SPECIAL("special"),
    BRACKETS("brackets"),
    NUMBER("number"),
    IDENTIFIER("identifier");

    private final String label;


    public static RegKeyEnum of(String label) {
        for (RegKeyEnum value : RegKeyEnum.values()) {
            if (StringUtils.equals(label, value.getLabel())) {
                return value;
            }
        }
        return null;
    }

}
