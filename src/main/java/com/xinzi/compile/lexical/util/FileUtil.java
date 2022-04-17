package com.xinzi.compile.lexical.util;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: xinzi
 * @Date: 2022/04/09/23:40
 */
public class FileUtil {

    public static Map<String, String> readProperties(String filename) {
        ResourceBundle bundle = ResourceBundle.getBundle(filename);
        String[] keys = {"number", "keyword", "special", "identifier"};
        Map<String, String> map = new LinkedHashMap<>();
        for (String key : keys) {
            String value = bundle.getString(key);
            map.put(key, value);
        }
        return map;
    }


    public static String readFile(File file) {
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s;
            while ((s = br.readLine()) != null) {
                result.append(System.lineSeparator()).append(s);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }


    public static void writeFile(String output, String filePath) {
        FileWriter fileWriter = null;
        try {
            // true表示不覆盖原来的内容，而是加到文件的后面。若要覆盖原来的内容，直接省略这个参数就好
            fileWriter = new FileWriter(filePath);
            fileWriter.write(output);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                assert fileWriter != null;
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
