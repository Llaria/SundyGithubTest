package sun.sundy.sundygithubtest;

import android.support.annotation.NonNull;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Example local unit Test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
//        assertEquals(4, 2 + 2);

//        String weight = "4.009";
//        System.out.println(Double.valueOf(weight));
//
//        System.out.println(1000 * Double.valueOf(weight) + "");
//        System.out.println(format(1000 * Double.parseDouble(weight),1) + "");
//
//        String ipAddress = "10,67,146,99";
//        String[] ip = ipAddress.split(",");
//        for (int i = 0; i < ip.length; i++) {
//            System.out.println(ip[i]);
//        }
//
//        String test = "—";
//        System.out.println(Arrays.toString(test.getBytes()));

        String order = "!0V0001A0002214000970000000000807000000000000000000000000010610071220131830192440B4102193803190323C4662D4662E";
        System.out.println(Arrays.toString(order.getBytes()));

        byte[] orderByte = {33, 48, 86, 48, 50, 56, 57, 65, 48, 48, 48, 50, 52, 52, 48, 48, 48, 48, 55, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 56, 48, 55,
                48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 49, 48, 54, 49, 48, 48, 55, 49, 50, 50, 48,
                49, 51, 49, 56, 51, 48, 49, 57, 50, 52, 52, 48, 66, 50, 51, 48, 50, 50, 51, 48, 50, 50, 51, 48, 50, 50, 52, 57, 48, 67, 52, 54, 54, 50, 68, 52, 54, 54, 50, 69, 13, 10, 3};
        System.out.println(new String(orderByte));

        getTotalAreaCode("散称37");
        checkNum("700002201500000984");
        System.out.println("".replace("Z", "0"));
    }

    private String getTotalAreaCode(String name) {
        StringBuilder stringBuilder = new StringBuilder();
        char[] nameChar = name.toCharArray();
        System.out.println("nameChar：" + Arrays.toString(nameChar));
        for (char c : nameChar) {
            if (isChinese(c))
                stringBuilder.append(toAreaCode(String.valueOf(c), true));
            else
                stringBuilder.append(toAreaCode(String.valueOf(c), false));
        }
        String code = stringBuilder.toString();
        if (code.contains("-")){
            System.out.println("存在非法字符");
            return "";
        }else {
            System.out.println("code值：" + code);
            return code;
        }
    }

    private String toAreaCode(String word, boolean isChina) {
        StringBuilder areaCode = new StringBuilder();
        byte[] bs;
        try {
            bs = word.getBytes("GB2312");
            System.out.println("bs字节数组（GB2312编码）：" + Arrays.toString(bs));
            for (byte b : bs) {
                int code = Integer.parseInt(Integer.toHexString(b & 0xFF), 16);
                System.out.println("单个字符的code值：" + code);
                if (isChina) {
                    int temp = code - 0x80 - 0x20;
                    if (temp < 10) {
                        areaCode.append("0").append(temp);
                    } else {
                        areaCode.append(temp);
                    }
                } else {
                    int temp = code - 0x20;
                    if (temp < 10) {
                        areaCode.append("030").append(temp);
                    } else {
                        areaCode.append("03").append(temp);
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return areaCode.toString();
    }

    /**
     * 根据Unicode编码判断中文汉字和符号
     */
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION;
    }

    /**
     * 散称打印条码规则校验
     */
    public static boolean checkNum(String barcode) {
        int odd = 0;
        int even = 0;
        for (int i = 0; i < barcode.length() - 1; i++) {
            int num = Integer.parseInt(String.valueOf(barcode.charAt(i)));
            if ((i + 1) % 2 == 0)
                odd = odd + num;
            else
                even = even + num;
        }
        int amount = odd * 3 + even;
        int Z = (amount / 10 + 1) * 10 - amount;
        boolean isCheckPass = String.valueOf(Z == 10 ? 0 : Z).equals(String.valueOf(barcode.charAt(barcode.length() - 1)));
        System.out.println(barcode + "======》》》校验码：" + (Z == 10 ? 0 : Z) + "校验是否通过：" + isCheckPass);
        return isCheckPass;
    }



    public static double format(double value, int precision) {
        BigDecimal b = new BigDecimal(Double.toString(value));
        BigDecimal temp = b.setScale(precision + 1, BigDecimal.ROUND_DOWN);
        double newValue = temp.setScale(precision, BigDecimal.ROUND_UP).doubleValue();
        return newValue;
    }


    @Test
    public void testCompare(){

        List<Student> list = new ArrayList<>();
        Student student1 = new Student();
        student1.setName("A");
        student1.setScore(80);
        Student student2 = new Student();
        student2.setName("B");
        student2.setScore(90);
        Student student3 = new Student();
        student3.setName("C");
        student3.setScore(60);
        list.add(student1);
        list.add(student2);
        list.add(student3);
        Collections.sort(list);
        System.out.println(list.toString());

        System.out.println("b.theme||\"-\"||b.designation AS designation, \n");


        System.out.println(String.format(Locale.CHINA, "%06d", (int) (4.60 * 100)));
        System.out.println((1.2 - 0.4) / 0.1);
        System.out.println((int)(mul(4.6 , 100,2)));
        double test = 9.7;
        System.out.println("" + test);
        String test2 = "10.2";
        System.out.println((int)(test * 100.0D));
    }

    public static double mul(double d1, double d2, int precision) {
        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        return format(bd1.multiply(bd2).doubleValue(), precision);
    }
}

class Student implements Comparable<Student>{

    private String name;
    private int score;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", score=" + score +
                '}';
    }

    @Override
    public int compareTo(@NonNull Student o) {
        return this.getScore() - o.getScore();
    }
}