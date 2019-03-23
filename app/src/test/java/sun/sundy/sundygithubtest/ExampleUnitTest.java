package sun.sundy.sundygithubtest;

import org.junit.Test;

import java.math.BigDecimal;

/**
 * Example local unit Test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
//        assertEquals(4, 2 + 2);

        String weight = "4.009";
        System.out.println(Double.valueOf(weight));

        System.out.println(1000 * Double.valueOf(weight) + "");
        System.out.println(format(1000 * Double.parseDouble(weight),1) + "");

        String ipAddress = "10,67,146,99";
        String[] ip = ipAddress.split(",");
        for (int i = 0; i < ip.length; i++) {
            System.out.println(ip[i]);
        }
    }


    public static double format(double value, int precision) {
        BigDecimal b = new BigDecimal(Double.toString(value));
        BigDecimal temp = b.setScale(precision + 1, BigDecimal.ROUND_DOWN);
        double newValue = temp.setScale(precision, BigDecimal.ROUND_UP).doubleValue();
        return newValue;
    }
}