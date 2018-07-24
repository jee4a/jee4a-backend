package com.jee4a.backend.common.util;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringUtil {
 


    /**
     * 将int转换为指定长度的字符串，前缀以0补位
     * 
     * @param intValue
     * @param strLength
     * @return
     */
    public static String int2Str(int intValue, int strLength) {
        if (strLength > 0) {
            char padding = '0';
            StringBuilder result = new StringBuilder();
            String fromStr = String.valueOf(intValue);
            for (int i = fromStr.length(); i < strLength; i++) {
                result.append(padding);
            }
            result.append(fromStr);
            return result.toString();
        } else {
            return null;
        }
    }

    /**
     * 生成32位编码
     * 
     * @return string
     */
    public static String getUUID(){
        String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
        return uuid;
    }

    /**
     * 生成6位编码
     * 
     * @return string
     */
    public static String getRandomNum(){
        String code ="";
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            code += random.nextInt(10);
        }
        return code;
    }

    /**
     * 随机指定范围内N个不重复的数
     * 
     * @param min
     *            指定范围最小值
     * @param max
     *            指定范围最大值
     * @param n
     *            随机数个数
     */
    public static int[] randomCommon(int min, int max, int n){
        if (n > (max - min + 1) || max < min) {
            return null;
        }
        int[] result = new int[n];
        int count = 0;
        while(count < n) {
            int num = (int) (Math.random() * (max - min)) + min;
            boolean flag = true;
            for (int j = 0; j < n; j++) {
                if(num == result[j]){
                    flag = false;
                    break;
                }
            }
            if(flag){
                result[count] = num;
                count++;
            }
        }
        return result;
    }

    /**
     * 银行卡
     * 
     * @param code
     * @return
     */
    public static String formatCardCode(String code){
        return  "************"+code.substring(code.length()-4, code.length());
    }

    /**
     * 身份证加*
     * 
     * @param code
     * @return
     */
    public static String formatCertificatesCode(String code){
        return  code.substring(0, 3)+"************"+code.substring(code.length()-4, code.length());
    }

    /**
     * 手机加*
     * 
     * @param telephone
     * @return
     */
    public static String formatTelephone(String telephone){
        return  telephone.substring(0, 3)+"*****"+telephone.substring(telephone.length()-4, telephone.length());
    }


    /**
     * 过滤特殊字符
     * 
     * @param str
     *            文本字符串
     * @return
     */
    public static String StrFilter(String str){
        String regEx = "[`~!@#$%^&*+=|{}':;',//[//].<>/?~！@#￥%……&*——+|{}【】‘；：”“’。，、？-]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return  m.replaceAll("").trim();
    }

    /**
     * 过滤特殊字符
     * 
     * @param dateStr
     *            日期字符转
     * @return
     */
    public static String DateStrFilter(String dateStr){
        String regEx = "[`~!@#$%^&*()+=|{}':;',.<>?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";// 注：允许‘/’和‘-’通过。
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(dateStr);
        return  m.replaceAll("").trim();
    }


    /**
     * isNumber
     * 
     * @param c
     *            char
     * @return boolean
     */
    public static boolean isNumber(char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * 生成批次号
     * 规则：(六位时分秒+三位毫秒+一位随机数)[10位]
     * @return
     */
    public static String generateBatchCode() {
        StringBuffer code = new StringBuffer();
        Calendar calendar = null;
        calendar =new GregorianCalendar();
        String hour = calendar.get(Calendar.HOUR_OF_DAY) + "";
        String minute = calendar.get(Calendar.MINUTE) + "";
        String second = calendar.get(Calendar.SECOND) + "";
        String mills = String.valueOf(System.currentTimeMillis());

        String threeMill = mills.substring(mills.length()-4, mills.length()-1);
        code.append(hour + minute + second + threeMill);

        Random random = new Random();
        code.append(random.nextInt(10));

        return code.toString();
    }
    
    /**
     *手机号码验证
     * @return
     */
    public static boolean checkMobilePhone(String mobliePhone) {
    	if(mobliePhone==null){
    			return false;
    		}
    	if(mobliePhone.trim().length()!=11){
    		return  false;
    	}
    	Pattern pattern=Pattern.compile("^1[3456789]\\d{9}$") ;
    	Matcher m=pattern.matcher(mobliePhone);
    	return m.matches();
    }
    
    /**
     *邮箱验证
     * @return
     */
    public static boolean checkEmail(String email) {
    	if(email==null){
    		return false;
    	}
    	Pattern pattern=Pattern.compile("^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$") ;
    	Matcher m=pattern.matcher(email);
    	return m.matches();
    }
    /**
     *匹配特殊字符
     * @return
     */
    public static boolean checkSpecialCharacter (String string) {
    	if(string==null){
    		return false;
    	}
    	Pattern pattern=Pattern.compile("[\u4e00-\u9fa5\\(|\\)|\\_|\\-|\\+|\\{|\\}|\\[|\\]\\ |a-zA-Z0-9]+$") ;
    	
    	Matcher m=pattern.matcher(string);
    	return m.matches();
    }
    /**
     *去掉手机号的空格 判断手机号中的特殊字符
     * @return
     */
    public static boolean checkMobilePhoneCharacter (String string) {
    	if(string==null){
    		return false;
    	}
    	Pattern pattern=Pattern.compile("[\\-|\\(|\\)|\\+|\\ |0-9]+$") ;
    	
    	Matcher m=pattern.matcher(string);
    	return m.matches();
    }
    
    /**
     *固话验证
     * @return
     */
    public static boolean checkPhone(String phone) {
    	if(phone==null){
    		return false;
    	}
    	Pattern pattern=Pattern.compile("(?:(\\(\\+?86\\))(0[0-9]{2,3}\\-?)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?)|"+ "(?:(86-?)?(0[0-9]{2,3}\\-?)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?)") ;
    	Matcher m=pattern.matcher(phone);
    	return m.matches();
    }
    

 
}

