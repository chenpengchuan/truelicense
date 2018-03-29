package de.schlichtherle.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class LicFileutils {
    private static Logger logger = LoggerFactory.getLogger(LicFileutils.class);
    /**
     * 读入二进制文件并生成对应的十六进制字符串文件
     * @param bytePath 二进制文件路径(输入文件)
     * @param hexPath 字符串文件路径(输出文件s)
     * @throws IOException
     */
    public static void byteFilleToHexFile(String bytePath, String hexPath) throws IOException {
        File file = new File(bytePath);
        FileInputStream inputs = new FileInputStream(file);
        byte[] arr = new byte[(int) file.length()];
        inputs.read(arr);
        inputs.close();
        if (!FileExit(hexPath)) {
            createFile(hexPath);
        }
        WriteFileByeLine(hexPath, bytesToHex(arr));
    }

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
                    logger.error(FilePath + " write hexFile failed!");
                }
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(FilePath + " write hexFile failed!");
            }
        }
    }
}