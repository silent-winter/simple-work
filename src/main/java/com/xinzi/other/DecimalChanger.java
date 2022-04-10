package com.xinzi.other;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description: 十进制转n进制
 * @Auther: xinzi
 * @Date: 2022/01/24/20:33
 */
public class DecimalChanger {

    private static final Map<Long, String> HEX_MAPPER = new HashMap<>();

    static {
        HEX_MAPPER.put(0L, "0");
        HEX_MAPPER.put(1L, "1");
        HEX_MAPPER.put(2L, "2");
        HEX_MAPPER.put(3L, "3");
        HEX_MAPPER.put(4L, "4");
        HEX_MAPPER.put(5L, "5");
        HEX_MAPPER.put(6L, "6");
        HEX_MAPPER.put(7L, "7");
        HEX_MAPPER.put(8L, "8");
        HEX_MAPPER.put(9L, "9");
        HEX_MAPPER.put(10L, "A");
        HEX_MAPPER.put(11L, "B");
        HEX_MAPPER.put(12L, "C");
        HEX_MAPPER.put(13L, "D");
        HEX_MAPPER.put(14L, "E");
        HEX_MAPPER.put(15L, "F");
    }


    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            String s = sc.next();
            System.out.println("Hex: " + decimalToOther(s, 16));
            System.out.println("Binary: " + decimalToOther(s, 2));
        }
        sc.close();
    }


    private static String decimalToOther(String decimalString, int target) {
        long originNum = Long.parseLong(decimalString);
        StringBuilder result = new StringBuilder();
        while (originNum > 0) {
            result.append(HEX_MAPPER.get(originNum % target));
            originNum /= target;
        }
        return result.reverse().toString();
    }

}
