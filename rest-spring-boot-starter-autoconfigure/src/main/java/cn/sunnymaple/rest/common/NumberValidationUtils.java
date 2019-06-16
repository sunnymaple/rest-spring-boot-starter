package cn.sunnymaple.rest.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据验证工具类
 * @auther: wangzb
 * @date: 2019/6/16 15:49
 */
public class NumberValidationUtils {
    /**
     * 判断是否为数字公用方法
     * @param regex 正则
     * @param orginal 目标数字
     * @return
     */
    private static boolean isMatch(String regex, String orginal){
        if(Utils.isEmpty(orginal)){
            return false;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher isNum = pattern.matcher(orginal);
        return isNum.matches();
    }

    /**
     * 是否为正整数
     * @param orginal 目标数字
     * @return
     */
    public static boolean isPositiveInteger(String orginal) {
        return isMatch("^\\+{0,1}[1-9]\\d*", orginal);
    }

    /**
     * 是否为负整数
     * @param orginal 目标数字
     * @return
     */
    public static boolean isNegativeInteger(String orginal) {
        return isMatch("^-[1-9]\\d*", orginal);
    }

    /**
     * 是否为整数
     * @param orginal 目标数字
     * @return
     */
    public static boolean isWholeNumber(String orginal) {
        return isMatch("[+-]{0,1}0", orginal) || isPositiveInteger(orginal) || isNegativeInteger(orginal);
    }

    /**
     * 是否为正小数
     * @param orginal 目标数字
     * @return
     */
    public static boolean isPositiveDecimal(String orginal){
        return isMatch("\\+{0,1}[0]\\.[1-9]*|\\+{0,1}[1-9]\\d*\\.\\d*", orginal);
    }

    /**
     * 是否为负小数
     * @param orginal 目标数字
     * @return
     */
    public static boolean isNegativeDecimal(String orginal){
        return isMatch("^-[0]\\.[1-9]*|^-[1-9]\\d*\\.\\d*", orginal);
    }

    /**
     * 是否为小数
     * @param orginal 目标数字
     * @return
     */
    public static boolean isDecimal(String orginal){
        return isMatch("[-+]{0,1}\\d+\\.\\d*|[-+]{0,1}\\d*\\.\\d+", orginal);
    }

    /**
     * 是否为数值（包括整数和小数）
     * @param orginal 目标数字
     * @return
     */
    public static boolean isNumeric(String orginal) {
        return isDecimal(orginal) || isWholeNumber(orginal);
    }
}
