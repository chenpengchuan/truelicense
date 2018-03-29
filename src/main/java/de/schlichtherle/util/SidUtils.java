package de.schlichtherle.util;

import sun.misc.BASE64Encoder;

import java.security.MessageDigest;

/**
 * 使用md5对获取到的机器信息进行加密，生成对应的sid
 */
public class SidUtils {
    private static String computerInfo = "";


    public static String getSid() {
        String cpu = HardWareUtils.getCPUSerial();
        String mac = HardWareUtils.getMac();
//        String Motherboard = HardWareUtils.getMotherboardSN();
        computerInfo = cpu + "," + mac;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] digest = messageDigest.digest(computerInfo.getBytes());
            // 通过base64编码成明文字符
            BASE64Encoder encoder = new BASE64Encoder();
//            System.out.println(encoder.encode(digest));
            return encoder.encode(digest);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("get this computer info failed");
            return null;
        }
    }
}
