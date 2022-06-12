package com.xinzi.compile.lexical;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: xinzi
 * @Date: 2022/06/12/11:19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Word {

    /**
     * 词法分析token类别码
     */
    private int type;

    /**
     * 原始词value
     */
    private String word;


    public static int typeOf(String regKey, String value) {
        RegKeyEnum regKeyEnum = RegKeyEnum.of(regKey);
        assert regKeyEnum != null;
        Map<String, Integer> valueMap = regKeyEnum.getValueMap();
        if (regKeyEnum == RegKeyEnum.IDENTIFIER) {
            return valueMap.get("identifier");
        } else if (regKeyEnum == RegKeyEnum.NUMBER) {
            return valueMap.get("number");
        }
        return valueMap.get(value);
    }

}
