package com.wwwjf.base.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    public static boolean isEmpty(final CharSequence s) {
        return s == null || s.length() == 0;
    }

    public static boolean isEmpty(final Object s) {
        return s == null || s.toString().length() == 0;
    }

    public static boolean isTrimEmpty(final String s) {
        return (s == null || s.trim().length() == 0);
    }

    public static boolean isTrimEmpty(Object object) {
        return (object == null || object.toString().length() == 0);
    }

    public static int length(final CharSequence s) {
        return s == null ? 0 : s.length();
    }

    public static int trimLength(final CharSequence s) {
        return s == null ? 0 : s.toString().trim().length();
    }

    public static boolean equals(final CharSequence s1, final CharSequence s2) {
        if (s1 == s2) return true;
        int length;
        if (s1 != null && s2 != null && (length = s1.length()) == s2.length()) {
            if (s1 instanceof String && s2 instanceof String) {
                return s1.equals(s2);
            } else {
                for (int i = 0; i < length; i++) {
                    if (s1.charAt(i) != s2.charAt(i)) return false;
                }
                return true;
            }
        }
        return false;
    }

    public static boolean equalsIgnoreCase(final String s1, final String s2) {
        return s1 == null ? s2 == null : s1.equalsIgnoreCase(s2);
    }

    /**
     * 字符串转换成int
     *
     * @param value 字符串
     */
    public static int toInt(final String value) {
        if (value == null || "".equals(value)) {
            return 0;
        }

        int result;
        try {
            result = Integer.valueOf(value);
        } catch (Exception e) {
            result = 0;
        }
        return result;
    }

    public static int toInt(BigInteger value) {
        if (value == null) {
            return 0;
        }
        return value.intValue();
    }

    public static int toInt(Integer value) {
        if (value == null) {
            return 0;
        }
        return value;
    }

    /**
     * 字符串转换成double
     *
     * @param value 字符串
     */
    public static double toDouble(final String value) {
        if (value == null || "".equals(value)) {
            return 0;
        }
        String temp = value;
        if (".".equals(value)) {
            return 0;
        }
        if (value.endsWith(".")) {
            temp = value.substring(0, value.length() - 1);
        }
        if (value.contains(",")){
            temp = value.replaceAll(",", "");
        }
        double result;
        try {
            result = new BigDecimal(temp).doubleValue();
        } catch (Exception e) {
            result = 0;
        }
        return result;
    }


    public static double toDouble(CharSequence text) {
        if (text == null) {
            return 0;
        } else {
            return toDouble(text.toString());
        }
    }

    /**
     * 转换成double
     *
     * @param value 字符串
     */
    public static double toDouble(final BigDecimal value) {
        if (value == null) {
            return 0;
        }
        return value.doubleValue();
    }

    public static BigDecimal toBigdecimal(final BigDecimal value) {
        if (value == null) {
            return new BigDecimal("0");
        }
        return value;
    }


    /**
     * 拆分字符串
     *
     * @param string key1=value1&key2=value2
     */
    public static Map<String, Object> getMap(String string) {
        Map<String, Object> result = new HashMap<>();
        String[] array = null;
        if (string.contains("&")) {
            array = string.split("&");
        }
        if (array == null || array.length <= 0) {
            return result;
        }
        for (String arr : array) {
            String[] split = arr.split("=");
            if (split.length >= 2) {
                result.put(split[0], split[1]);
            }
        }

        return result;
    }


    /**
     * 拆分字符串
     *
     * @param string value1,value2 // value1;value2
     */
    public static List<String> getList(String string) {
        List<String> result = new ArrayList<>();
        String[] array = null;
        if (string.contains(",")) {
            array = string.split(",");
        } else if (string.contains(";")) {
            array = string.split(";");
        }
        if (array == null || array.length <= 0) {
            result.add(string);
            return result;
        }
        result.addAll(Arrays.asList(array));
        return result;
    }

    public static String toLowerCase(String string) {
        if (string == null || "".equals(string)) {
            return "";
        }
        return string.toLowerCase(Locale.getDefault());
    }


    public static String toUpperCase(String string) {
        if (string == null || "".equals(string)) {
            return "";
        }
        return string.toUpperCase(Locale.getDefault());
    }

    /**
     * 带占位符字符串
     *
     * @param pattern   "aa{0},{1},{2}bbcc"
     * @param arguments 参数值
     */
    public static String format(String pattern, Object... arguments) {
        return MessageFormat.format(pattern, arguments);
    }

    /**
     * 格式化字符串
     *
     * @param pattern   "aa%.2fbbcc"
     * @param arguments 参数值
     */
    public static String format2(String pattern, Object... arguments) {
        return String.format(Locale.getDefault(), pattern, arguments);
    }


    public static String valueOf(String s) {
        return s == null ? "" : s;
    }

    public static String valueOf(Object object) {
        return object == null ? "" : object.toString();
    }

    public static String valueOf(BigDecimal value) {
        return value == null ? "" : value.toString();
    }

    /**
     * 去除多余的 0
     *
     * @param value value
     */
    public static String toPlainString(BigDecimal value) {
        if (value == null) {
            return "";
        }
        /*if (value.doubleValue() == 0) {
            return "0";
        }*/
        return value.stripTrailingZeros().toPlainString();
    }

    public static String valueOf(Integer value) {
        return value == null ? "" : value.toString();
    }

    public static String valueOf(double d) {

        return String.valueOf(d);
    }

    public static String valueOf(long l) {
        return String.valueOf(l);
    }

    public static String valueOf(int i) {
        return String.valueOf(i);
    }

    public static BigDecimal abs(BigDecimal value) {
        if (value == null) {
            return new BigDecimal(0);
        }
        return value.abs();
    }

    public static boolean isNumber(Object obj) {
        if (obj instanceof Number) {
            return true;
        } else if (obj instanceof String) {
            try {
                Double.parseDouble((String) obj);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }


    public static long toLong(String value) {
        if (value == null || value.isEmpty()) {
            return 0;
        }
        return Long.parseLong(value);
    }

    public static boolean IsContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }
}
