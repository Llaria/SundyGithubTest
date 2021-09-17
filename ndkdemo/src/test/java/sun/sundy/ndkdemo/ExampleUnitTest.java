package sun.sundy.ndkdemo;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws InterruptedException {
        for (int i = 0; i < 6; i++) {
            randomTest();
            Thread.sleep(5000);
        }
    }

    private void randomTest() {
        List<String> name = new ArrayList<>();
        name.add("李虎");
        name.add("吴希有");
        name.add("雷超");
        name.add("陈理想");
        name.add("章云国");
        name.add("黄顺");
        name.add("王凯旋");
        name.add("陈雁峰");
        name.add("孙迪");
        name.add("温凯");
        name.add("龚向建");
        name.add("汪志为");

        Random random = new Random(new Date().getTime());
        for (int i = 11; i > 0; i--) {
            int ran = random.nextInt(i + 1);
            System.out.println(name.get(ran));
            name.remove(ran);
        }
        System.out.println(name.get(0));
        System.out.println("==================");
    }
}