

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串处理类
 *
 * @author Yuan wenyu
 * @version 1.0 2007.11.19
 * @copyright founder Ltd. (2007)
 */
public class StringUtil {

    /**
     * 方法名： getNumberForMachineSequence<br>
     * 描述  ： 计算两个字节的机器序列数 <br>
     *<br>
     * @param str <br>
     * @return int<br>
     */
    public static int getNumberForMachineSequence(String str) {
        if (str.length() != 4) {
            return 0;
        }
        String tmp1 = str.substring(0, 2);
        String tmp2 = str.substring(2, 4);
        str = tmp2 + tmp1;
        return  Integer.valueOf(str, 16);
    }

    /**
     * 判断字符串是否为空
     *
     * @param str
     * @return
     * @by 张金昕
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0)
            return true;
        for (int i = 0; i < strLen; i++)
            if (!Character.isWhitespace(str.charAt(i)))
                return false;

        return true;
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 以type为分界符分离str字符串,返回分离后的数组
     *
     * @param str  待处理字符串
     * @param type 分界符
     * @return array    处理后的字符串数组
     */
    public static String[] split(String str, String type) {

        int begin = 0;
        int pos = 0;
        String tempstr = "";
        String[] array = null;
        Vector vec = null;
        int len = str.trim().length();
        str = str + type;

        if (len > 0) {
            while (str.length() > 0) {

                if (vec == null) {
                    vec = new Vector();
                }

                pos = str.indexOf(type, begin);
                tempstr = str.substring(begin, pos);
                str = str.substring(pos + 1, str.length());
                vec.add(tempstr);
            }
        } else {
            vec = new Vector();
            vec.add("");
        }
        if (vec != null) {
            array = new String[vec.size()];

            for (int i = 0; i < vec.size(); i++) {
                array[i] = (String) vec.get(i);
            }
        } else {
            array = new String[0];
        }

        return array;
    }

    /**
     * 按长度把字符串前补0
     *
     * @param s 需要补位字符串对象
     * @return len   该字符串的长度
     */
    public static String len(String s, int len) {

        if (!notNull(s))
            s = "";

        int length = len - s.length();
        for (int i = 0; i < length; i++) {
            s = "0" + s;
        }
        return s;
    }

    /**
     * 判断字符串是否为空
     *
     * @param s 需要判断字符串对象
     * @return true  表示不为空 false 为空
     * @throws Exception
     */
    public static boolean notNull(String s) {

        if (s == null || s.trim().length() <= 0 || "".equals(s)
                || "null".equals(s))
            return false;
        else
            return true;
    }

    /**
     * 返回一个布尔值，表示两个字符串是否相等
     *
     * @param objstr 字符串对象
     * @param bjstr  字符串对象
     * @return false  表示不相等 true 相等
     * @throws Exception
     */
    public static boolean equals(String objstr, String bjstr) {

        boolean lean = false;
        lean = (objstr != null) && objstr.equals(bjstr);

        return lean;
    }

    /**
     * 替换source中的str1为str2
     *
     * @param source 待替换的字符串
     * @param str1   待替换的字符
     * @param str2   替换的字符
     * @return java.lang.String 替换后的字符串
     */
    public static String replace(String source, char str1, String str2) {
        return replace(source, String.valueOf(str1), str2);
    }

    /**
     * 替换source中的str1为str2
     *
     * @param source 待替换的字符串
     * @param str1   待替换的字符
     * @param str2   替换的字符
     * @return java.lang.String 替换后的字符串
     */
    public static String replace(String source, String str1, String str2) {
        if (source == null)
            return "";
        String desc = "";
        int posstart = 0;
        int posend = source.indexOf(str1, 0);
        int len = source.length();
        while (posend >= 0 && posstart < len) {
            desc += source.substring(posstart, posend) + str2;
            posstart = posend + str1.length();
            posend = source.indexOf(str1, posstart);
        }
        if (posstart < len)
            desc += source.substring(posstart, len);
        return desc;
    }

    /**
     * 替换source中的"\n"为"换行符"
     *
     * @param content 待替换的字符串
     * @return java.lang.String 替换后的字符串
     */
    public static String replace(String content) {
        String[] tempValue = null;
        if (content != null) {
            if (content.indexOf("\n") != -1) {
                tempValue = StringUtil.split(content, "\n");
            }
            if (tempValue != null && tempValue.length > 0) {
                content = "";
                for (int i = 0; i < tempValue.length; i++) {
                    content += tempValue[i] + "<br>";
                }
                content = content.substring(0, content.length() - 4);
            }
        }
        return content;
    }

    /**
     * 将字符串列表转换成字符串数组
     *
     * @param list List 字符串列表
     * @return String[]
     */
    public static String[] list2Array(List list) {
        String[] strs = new String[list.size()];
        for (int i = 0; i < strs.length; i++) {
            strs[i] = (String) list.get(i);
        }
        return strs;
    }

    /**
     * 将字符串数组转换成字符串列表
     *
     * @param array String[] array 字符串数组
     * @return List  字符串列表
     */
    public static List array2List(String[] array) {
        List list = null;
        if (array != null) {
            list = new ArrayList(array.length);
            for (int i = 0; i < array.length; i++) {
                list.add(list.get(i));
            }
        }
        return list;
    }

    /**
     * 过滤Html的特殊字符
     */
    public static String htmlEscape(String in) {
        if (null == in || "".equals(in)) {
            return "";
        }
        StringBuffer out = new StringBuffer();
        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);
            switch (c) {
                case '"':
                    out.append("&quot;");
                    break;
                case '&':
                    out.append("&amp;");
                    break;
                case '<':
                    out.append("&lt;");
                    break;
                case '>':
                    out.append("&gt;");
                    break;
                default:
                    out.append(c);
                    break;
            }
        }
        return out.toString();
    }

    /**
     * 取String最后几位
     */
    public static String getStrLast(String str, int i) {
        return str.substring(str.length() - i, str.length());
    }

    /**
     * 判断是该字符串是否是图片或者是图片连接
     * jpg,bmp,gif,jpeg
     */
    public static boolean isImage(String str) {
        boolean flag = false;
        String type1 = StringUtil.getStrLast(str, 4);
        if (type1.equals(".jpg") || type1.equals(".JPG")) {
            flag = true;
        }
        if (type1.equals(".bmp") || type1.equals(".BMP")) {
            flag = true;
        }
        if (type1.equals(".gif") || type1.equals(".GIF")) {
            flag = true;
        }

        String type2 = StringUtil.getStrLast(str, 5);
        if (type2.equals(".jpeg") || type2.equals(".GPEG")) {
            flag = true;
        }
        return flag;
    }

    public static String urlFilter(String str) {
        String temp = "http://www.";
        String temp1 = "www.";
        String temp2 = "http://";
        if (str.indexOf(temp) > -1) {

        } else {
            if (str.indexOf(temp2) > -1) {

            } else {
                if (str.indexOf(temp1) > -1) {
                    str = "http://" + str;
                } else {
                    str = temp2 + str;
                }
            }
        }

        String s = str.substring(7);
        if (s.indexOf("/") > 0) {

        } else {
            char c = str.charAt(str.length() - 1);

            if ("/".equals(String.valueOf(c))) {

            } else {
                str = str + "/";
            }
        }

        return str;
    }

    public static String rssUrlEcode(String rss) {
        if (isBlank(rss)) {
            return "";
        }
        String re = "";
        try {
            re = java.net.URLEncoder.encode("\"" + rss + "\"", "utf-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println("转码异常,UnsupportedEncodingException");
            e.printStackTrace();
        }
        return re;
    }

    /**
     * Replaces the first substring of this string that matches the given key
     * with the given replacement.
     *
     * @param str         The String to be replaced
     * @param key         Key within the String to be searched and replaced
     * @param replacement The String used to replace
     * @return The String with the first occurence of the key value replaced.
     */
    public static String replaceFirst(String str, String key, String replacement) {
        StringBuffer result = new StringBuffer(str);

        int pos = str.indexOf(key);

        if (pos >= 0) {
            result.replace(pos, pos + key.length(), replacement);
            // System.out.println( "result = " + result );
        }
        return result.toString();
    }

    public static String replaceChar(String str) {

        return str.replace("\\", "/");
    }

    /**
     * 对double数据进行取精度.
     * <p>
     * For example: <br>
     * double value = 100.345678; <br>
     * double ret = round(value,4,BigDecimal.ROUND_HALF_UP); <br>
     * ret为100.3457 <br>
     *
     * @param value        double数据.
     * @param scale        精度位数(保留的小数位数).
     * @param roundingMode 精度取值方式.
     * @return 精度计算后的数据.
     */
    public static double round(double value, int scale, int roundingMode) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(scale, roundingMode);
        double d = bd.doubleValue();
        bd = null;
        return d;
    }

    /**
     * 得到文件名的后缀
     *
     * @param string
     */
    public static String getHouzhui(String string) {

        int t = string.lastIndexOf(".");
        String str = string.substring(t + 1);
        return str;
    }

    /**
     * 截取字符串作为文件标题
     */
    public static String getTitleSubString(String str) {
        Pattern p = Pattern.compile("\\s*|\t*|\r*|\n*");
        Matcher m = p.matcher(str);
        String after = m.replaceAll("");

        String temp = after.replaceAll("\\p{P}", "");

        return temp.substring(0, 10).trim();
    }

    //得到字符串里的大写个数
    public static int getUpperCaseNum(String str) {
        int count = 0;
        Pattern p = Pattern.compile("[A-Z]");
        Matcher m = p.matcher(str);
        while (m.find()) {
            count++;
        }
        return count;
    }

    //得到字符串里的小写个数
    public static int getLowerCaseNum(String str) {
        int count = 0;
        Pattern p = Pattern.compile("[a-z]");
        Matcher m = p.matcher(str);
        while (m.find()) {
            count++;
        }
        return count;
    }

    //得到字符串里的数字个数
    public static int getFigureNum(String str) {
        int count = 0;
        Pattern p = Pattern.compile("\\d");
        Matcher m = p.matcher(str);
        while (m.find()) {
            count++;
        }
        return count;
    }

    //得到字符串里特殊字符个数
    public static int getSpecialCharNum(String str) {
        int count = 0;
        Pattern p = Pattern.compile("[\\W_]");
        Matcher m = p.matcher(str);
        while (m.find()) {
            count++;
        }
        return count;
    }

    //去掉最后一个字符
    public static String removeLastChar(String str) {
        if (str != "") {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    /**
     * 替换文档的可变部分 add by wde 2009-7-6
     *
     * @param content        原来的文本
     * @param markersign     标记符号
     * @param replacecontent 替换的内容
     *                       用replacecontent替换markersign
     * @return
     */
    public static String replaceRTF(String content, String markersign, String replacecontent) {
        String rc = replacecontent;
        String target = "";
        markersign = "%{" + markersign + "}%";
        target = content.replace(markersign, rc);
        return target;
    }

    /**
     * 去除重复项
     *
     * @param list
     * @return list
     */
    public static List removeDuplicateWithOrder(List list) {
        Set set = new HashSet();
        List newList = new ArrayList();
        for (Iterator iter = list.iterator(); iter.hasNext(); ) {
            Object element = iter.next();
            if (set.add(element))
                newList.add(element);
        }
        return newList;
    }

    /**
     * 字节转换成字符串以兆的形式显示
     *
     * @param size
     * @return
     */
    public static String formate(int size) {
        StringBuffer sb = new StringBuffer();
        sb.append(String.valueOf((int) (size / 1024)));
        sb.append("M");
        return sb.toString();
    }

    /**
     * 根据stringSource包含的字符与number生成固定长度的随机字符串
     *
     * @param length
     * @param stringSource
     * @param flag
     * @return
     */
    public static String getFixedSizeRandomStr(int length, String stringSource, int flag) {
        Random random = new Random();
        int le = length;
        StringBuffer sBuffer = new StringBuffer();
        if (length > 0) {
            Map<Integer, Integer> map = new HashMap<Integer, Integer>();
            //int mark = -11;
            for (int i = 0; i <= stringSource.length() + 1; i++) {
                map.put(i, 0);
            }
            if (sBuffer.length() < length) {
                for (int i = 0; i < le; i++) {
                    int number = random.nextInt(stringSource.length());
                    map.put(number, map.get(number) + 1);

                    if (map.get(number) > flag) {
                        le++;
                        continue;
                    }
                    sBuffer.append(stringSource.charAt(number));
                }
            }
        }
        return sBuffer.toString();
    }

    public static String xssEscape(String value) {
        if (value != null) {
            // NOTE: It's highly recommended to use the ESAPI library and uncomment the following line to
            // avoid encoded attacks.
            // value = ESAPI.encoder().canonicalize(value);

            // Avoid null characters
            value = value.replaceAll("", "");

            // Avoid anything between script tags
            Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid anything in a src='...' type of e­xpression
            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Remove any lonesome </script> tag
            scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // Remove any lonesome <script ...> tag
            scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid eval(...) e­xpressions
            scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid e­xpression(...) e­xpressions
            scriptPattern = Pattern.compile("e­xpression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid javascript:... e­xpressions
            scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid vbscript:... e­xpressions
            scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid onload= e­xpressions
            scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
        }
        return value;
    }

    /**
     * 方法名： getMacStrFromStr<br>
     * 描述  ： 将给定的字符串按照 mac 地址格式输出 <br>
     * <br>
     *
     * @param str <br>
     * @return java.lang.String<br>
     */
    public static String getMacStrFromStr(String str) {
        if (str.trim().length() != 12) {
            return null;
        }
        str = str.trim();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i <= 5; i++) {
            sb.append(str.substring(i * 2, (i + 1) * 2));
            if (i != 5) {
                sb.append(":");
            }
        }
        return sb.toString();
    }


    /**
     * 方法名： getVStrFromTLVByT <br>
     * 描述  ： TLV格式逐个解析，直到取出的字段的T为指定值，返回V <br>
     * <br>
     *
     * @param str  <br>
     * @param flag <br>
     * @return java.lang.String<br>
     */
    public static String getVStrFromTLVByT(String str, String flag) {
        try {
            if (str.startsWith(flag)) {
                int len = Integer.parseInt(str.substring(2, 4), 16);
                if (len == 0) {
                    return "e99a90e8978f"; //‘隐藏’的16进制
                }
                if (str.length() < len * 2 + 4) {
                    return null;
                }
                String value = str.substring(4, len * 2 + 4);
                return value;
            } else {
                if (str.length() < 4) {
                    return null;
                }
                int len = Integer.parseInt(str.substring(2, 4), 16);
                if (str.length() < len * 2 + 4) {
                    return null;
                }
                str = str.substring(len * 2 + 4);
                return getVStrFromTLVByT(str, flag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 方法名： hexToASC<br>
     * 描述  ： 十六进制转为ASCII码 <br>
     * <br>
     *
     * @param hexString <br>
     * @return java.lang.String<br>
     */
    public static String hexToASC(String hexString) {
        if ("e99a90e8978f".equals(hexString)) {
            return "隐藏";
        }
        String result = "";
            int max = hexString.length() / 2;
        for (int i = 0; i < max; i++) {
            int tmp = Integer.parseInt(hexString.substring(i * 2, (i + 1) * 2), 16);
            if (tmp >= 127 || tmp <= 32) {
                //超过该范围的全部以点号代替
                tmp = 46;
            }
            char c = (char) tmp;
            result += c;
        }
        return result;
    }

    public static String repalaceAllByStr(String content,String start,String end){
        int sn = content.indexOf(start);
        int en = content.indexOf(end,sn);
        String temp = content.substring(sn, en+end.length());
        content=content.replace(temp, "");
        return content;
    }

    public static void main(String[] args) {
        String str = "\n" +
                "<div class=\"pageContent\" style=\"text-align: left;\">\n" +
                "    <div class=\"pageFormContent nowrap\"  layouth=\"5\" >\n" +
                "        <h2 class=\"contentTitle\">\n" +
                "            <fmt:formatDate value=\"${riskReportData.risk.endTime}\" pattern=\"yyyy年MM月dd日 HH:mm\"/>&nbsp;-&nbsp;${riskReportData.risk.groupName}&nbsp;-&nbsp;工控信息安全合规检查综述报表\n" +
                "            <div id=\"replaceAll\">\n" +
                "                <a href=\"rest/risk/report_details?risk_id=${riskReportData.risk.id}\" style=\"float: right\" class=\"file\" target=\"navTab\" rel=\"scoreDetailDig\" title=\"查看详细报表\">&nbsp;&nbsp;&nbsp;&nbsp;查看详细报表</a>\n" +
                "                <a href=\"rest/risk/download?riskId=${riskReportData.risk.id}&type_str=report\" style=\"float: right\" target=\"dialog\" class=\"file\" rel=\"riskDetailDig\" mask=\"true\">下载报表</a>\n" +
                "            </div>\n" +
                "        </h2>";
        String s = StringUtil.repalaceAllByStr(str,"<div id=\"replaceAll\">","</div>\n");
        System.out.println(s);
    }


}
