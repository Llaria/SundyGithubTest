package sun.sundy.sundygithubtest.javademo;

import java.util.Arrays;

public class JavaDemo {

    private int count = 10;
    private static final String TAG = "test";

    public static void main(String[] args) {

        int[] sort = {3,4,1,2,5,7,6,9,8};
        for (int i = 0; i < sort.length; i++) {
            for (int k = i+1; k < sort.length; k++) {
                if (sort[i] < sort[k]){
                    int temp = sort[i];
                    sort[i] = sort[k];
                    sort[k] = temp;
                }
            }
        }
        System.out.println(Arrays.toString(sort));
//        try {
//            ThreeThread threeThread = new ThreeThread();
//            threeThread.setDaemon(true);
//            threeThread.start();
//            Thread.sleep(5000);
//            System.out.println("结束");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < 30; i++) {
//                    try {
//                        Thread.sleep(1000);
//                        System.out.println("j=" + i);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();

//        TwoTread twoTread = new TwoTread();
//        twoTread.start();
//        try {
//            Thread.sleep(10);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        twoTread.interrupt();

//        OneThread oneThread = new OneThread();
//
//        Thread one = new Thread(oneThread,"A");
//        Thread two = new Thread(oneThread,"B");
//        Thread three = new Thread(oneThread,"C");
//        Thread four = new Thread(oneThread,"D");
//        Thread five = new Thread(oneThread,"E");
//
//        one.start();
//        two.start();
//        three.start();
//        four.start();
//        five.start();


//        MyThread myThread = new MyThread();
//        myThread.start();
//        myThread.run();
//        System.out.println("=========>>>>>开启线程");
//
//        for (int i = 0; i < 20; i++) {
//            try {
//                Thread.sleep(1000);
//                System.out.println("======》》》》" + Thread.currentThread().getName());
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//        myThread.run();
//        CountThread countThread1 = new CountThread(1);
//        CountThread countThread2 = new CountThread(2);
//        CountThread countThread3 = new CountThread(3);
//        CountThread countThread4 = new CountThread(4);
//        CountThread countThread5 = new CountThread(5);
//        CountThread countThread6 = new CountThread(6);
//        CountThread countThread7 = new CountThread(7);
//        CountThread countThread8 = new CountThread(8);
//        CountThread countThread9 = new CountThread(9);
//        CountThread countThread10 = new CountThread(10);
//        CountThread countThread11 = new CountThread(11);
//
//        countThread1.start();
//        countThread2.start();
//        countThread3.start();
//        countThread4.start();
//        countThread5.start();
//        countThread6.start();
//        countThread7.start();
//        countThread8.start();
//        countThread9.start();
//        countThread10.start();
//        countThread11.start();
    }
}

class ThreeThread extends Thread{

    private int i = 0;

    @Override
    public void run() {
        super.run();
        try {
            while (true) {
                i++;
                System.out.println("i=" + i);
                Thread.sleep(1000);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}


class TwoTread extends Thread {

    @Override
    public void run() {
        super.run();
        try {
            for (int i = 0; i < 1000; i++) {
                if (this.isInterrupted())
                    throw new InterruptedException();

                System.out.println("数字：" + i);
            }
            System.out.println("结束");
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("异常停止" + e.getMessage());
        }
    }
}


class OneThread extends Thread {

    private int count = 5;

    @Override
    synchronized public void run() {
        super.run();
        count--;
        System.out.println(Thread.currentThread().getName() + "计算" + count);
    }
}


class CountThread extends Thread {

    private int i;

    public CountThread(int i) {
        this.i = i;
    }

    @Override
    public void run() {
        super.run();
        System.out.println(i);
    }
}

class MyThread extends Thread {

    @Override
    public void run() {
        super.run();
        for (int i = 0; i < 20; i++) {
            try {
                Thread.sleep(1000);
                System.out.println("=====???" + Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}