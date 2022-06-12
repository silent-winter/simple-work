package com.xinzi.compile.lexical;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

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

    KEYWORD("keyword", new ImmutableMap.Builder<String, Integer>()
            .put("main", 1)
            .put("int", 2)
            .put("char", 3)
            .put("if", 4)
            .put("else", 5)
            .put("for", 6)
            .put("while", 7)
            .put("end", 0)
            .build()),
    SPECIAL("special", new ImmutableMap.Builder<String, Integer>()
            .put("=", 21).put("+", 22).put("-", 23).put("*", 24).put("/", 25)
            .put("(", 26).put(")", 27).put("[", 28).put("]", 29).put("{", 30).put("}", 31)
            .put(",", 32).put(":", 33).put(";", 34)
            .put(">", 35).put("<", 36).put(">=", 37).put("<=", 38).put("==", 39).put("!=", 40)
            .build()),
    BRACKETS("brackets", ImmutableMap.of("(", 26, ")", 27)),
    NUMBER("number", ImmutableMap.of("number", 20)),
    IDENTIFIER("identifier", ImmutableMap.of("identifier", 10));


    private final String label;
    private final Map<String, Integer> valueMap;


    public static RegKeyEnum of(String label) {
        for (RegKeyEnum value : RegKeyEnum.values()) {
            if (StringUtils.equals(label, value.getLabel())) {
                return value;
            }
        }
        return null;
    }

}
