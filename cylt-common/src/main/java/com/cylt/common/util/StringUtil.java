package com.cylt.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {


    /**
     * 判断该字符串是否为数字
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
}
