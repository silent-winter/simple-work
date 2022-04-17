package com.xinzi.compile.lexical;

import com.xinzi.compile.lexical.util.FileUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: xinzi
 * @Date: 2022/04/10/11:16
 */
public class Application {

    public static void main(String[] args) {
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer();
        // 读取程序文件
        String programme = FileUtil.readFile(new File("E:\\JavaEE\\project\\simple-work\\src\\main\\resources\\source_char.txt")).trim();
        // 读取正则表达式配置文件
        Map<String, String> regexpMap = FileUtil.readProperties("grammer");

        StringBuilder output = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < programme.length(); i++) {
            char c = programme.charAt(i);
            if (c == '\n' || c == '\r' || c == ' ') {
                if (StringUtils.isBlank(temp)) {
                    output.append(c);
                    continue;
                }
                for (String key : regexpMap.keySet()) {
                    // 自动机逐个分析
                    boolean analyze = lexicalAnalyzer.analyze(regexpMap.get(key), temp.toString(), true);
                    if (analyze) {
                        output.append('<').append(StringUtils.upperCase(key)).append(":(").append(temp).append(")>");
                        break;
                    }
                }
                output.append(c);
                temp = new StringBuilder();
            } else if (c == '{') {
                // 处理注释
                output.append("<ANNOTATION>");
                int j = i + 1;
                while (true) {
                    if (programme.charAt(j) == '}') {
                        i = j + 1;
                        break;
                    }
                    j++;
                }
            } else {
                temp.append(c);
            }
        }
        // 分析最后一个词
        if (StringUtils.isNoneBlank(temp)) {
            for (String key : regexpMap.keySet()) {
                // 自动机逐个分析
                boolean analyze = lexicalAnalyzer.analyze(regexpMap.get(key), temp.toString(), true);
                if (analyze) {
                    output.append('<').append(StringUtils.upperCase(key)).append(":(").append(temp).append(")>");
                    break;
                }
            }
        }
        System.out.println(output);
        FileUtil.writeFile(output.toString(), "E:\\JavaEE\\project\\simple-work\\src\\main\\resources\\source_token.txt");
    }

}
