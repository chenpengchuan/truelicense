package de.schlichtherle.util;

import java.io.*;

public class LicFileutils {

    /**
     * 二进制转16进制字符串
     *
     * @param in
     * @return
     */
    public static String bytesToHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for (byte b : in) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    /**
     * 十六进制字符串转换成二进制
     *
     * @param s
     * @return
     */
    public static byte[] hexStringToByteArray(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index, index + 2), 16);
            b[i] = (byte) v;
        }
        return b;
    }

    /**
     * 判断文件是否存在
     *
     * @return
     */
    public static boolean FileExit(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 创建文件
     *
     * @param
     * @return
     */
    public static boolean createFile(String fileName) {
        File file = new File(fileName);
        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 读取怎个文件，一起以一个串的形式返回
     *
     * @param filePath
     * @return
     */
    public static String ReadFile(String filePath) {
        String str = "";
        try {
            // String encoding = "GBK";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) { // 判断是否是文件
                InputStreamReader read = new InputStreamReader(new FileInputStream(file));
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    str += lineTxt;
                }
                // System.out.println(str);
                bufferedReader.close();
                read.close();
            } else {
                System.out.println(filePath + "不是文件");
            }
        } catch (Exception e) {
            System.out.println("读文件出错");
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 一行一行的读取文件
     *
     * @param filePath
     * @throws IOException
     */
    public static void ReadFileByline(String filePath) throws IOException {
        File file = new File(filePath);// 加载文件
        try {
            String str = null;
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((str = br.readLine()) != null) {
                System.out.println(str);
            }
            br.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 一行一行的写入文件
     *
     * @param FilePath
     * @param content
     * @throws IOException
     */
    public static void WriteFileByeLine(String FilePath, String content) {
        FileWriter writer = null;
        try {
            // true代表一追加的方式写入
            writer = new FileWriter(FilePath, false);
            writer.write(content + "\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                } else {
                    System.out.println(FilePath + "写入失败");
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(FilePath + "写入不成功");
            }
        }
    }
}