package xr.weweather.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

//将API返回的数据转换成字符串
public class NetStreamToStringUtil {

    public static String streamToString(InputStream in) {
        String netCode = "";
        try {
            // 字节流对象
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            byte[] b = new byte[1024];
            int length = 0;
            while ((length = in.read(b)) != -1) {
                out.write(b, 0, length);
                out.flush();
            }
            // 将流对象转换为String
            netCode = out.toString();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 返回结果
        return netCode;
    }
}
