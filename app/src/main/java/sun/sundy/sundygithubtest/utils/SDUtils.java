package sun.sundy.sundygithubtest.utils;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SDUtils {
    /**
     * 将assets中的识别库复制到SD卡中
     *
     * @param path 要存放在SD卡中的 完整的文件名。这里是"/storage/emulated/0//tessdata/chi_sim.traineddata"
     * @param name assets中的文件名 这里是 "chi_sim.traineddata"
     */
    public static void assets2SD(Context context, String path, String name) {
        //如果存在就删掉
        File f = new File(path);
        if (f.exists()) {
            f.delete();
        }
        if (!f.exists()) {
            File p = new File(f.getParent());//返回此抽象路径名父目录的路径名字符串
            if (!p.exists()) {
                p.mkdirs();//建立多级文件夹
            }
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        InputStream is = null;
        OutputStream os = null;
        try {
            //打开assets文件获得一个InputStream字节输入流
            is = context.getAssets().open(name);
            File file = new File(path);
            // 创建一个向指定 File 对象表示的文件中写入数据的文件输出流
            os = new FileOutputStream(file);
            byte[] bytes = new byte[2048];
            int len = 0;
            //从输入流中读取一定数量的字节，并将其存储在缓冲区数组bytes中
            //如果因为流位于文件末尾而没有可用的字节，则返回值-1
            while ((len = is.read(bytes)) != -1) {
                //将指定byte数组中从偏移量off开始的len个字节写入此缓冲的输出流
                os.write(bytes, 0, len);
            }
            //java在使用流时,都会有一个缓冲区,按一种它认为比较高效的方法来发数据:把要发的数据先放到缓冲区,
            //缓冲区放满以后再一次性发过去,而不是分开一次一次地发
            //flush()强制将缓冲区中的数据发送出去,不必等到缓冲区满
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭输入流和输出流
                if (is != null)
                    is.close();
                if (os != null)
                    os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}