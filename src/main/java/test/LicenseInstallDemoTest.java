package test;

import de.schlichtherle.demo.LicenseInstallDemo;
import de.schlichtherle.util.PropertiesFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class LicenseInstallDemoTest {
    public static String propFilePath = "E:\\license-conf-dir\\verify.properties";

    private static Map<String, String> setParam() {
        Map<String, String> map = new HashMap<>();
        PropertiesFile propertiesFile = new PropertiesFile(propFilePath);
        Properties prop = propertiesFile.Loading();
        map.put("PUBLICALIAS", prop.getProperty("PUBLICALIAS"));
        map.put("STOREPWD", prop.getProperty("STOREPWD"));
        map.put("SUBJECT", prop.getProperty("SUBJECT"));
        map.put("licPath", prop.getProperty("licPath"));
        map.put("pubPath", prop.getProperty("pubPath"));
        return map;
    }

    public static void main(String[] args) throws IOException {
        LicenseInstallDemo indtalldemo = new LicenseInstallDemo();
        Map<String,String> map = setParam();
        File file = new File(map.get("licPath"));
        FileInputStream inputStream = new FileInputStream(file);
        byte[] arr = new byte[(int)file.length()];
        inputStream.read(arr);
        String result = indtalldemo.install(map,arr);
        System.out.println(result);
    }
}
