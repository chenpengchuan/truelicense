package test;

import de.schlichtherle.demo.LicenseInfoDemo;
import de.schlichtherle.util.PropertiesFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class LicenseInfoDemoTest {

    public static String propFilePath = "E:\\license-conf-dir\\verify.properties";

    private Map<String, String> setParam(){
        Map<String, String> map = new HashMap<>();
        PropertiesFile propertiesFile = new PropertiesFile(this.propFilePath);
        Properties prop = propertiesFile.Loading();
        map.put("PUBLICALIAS", prop.getProperty("PUBLICALIAS"));
        map.put("STOREPWD", prop.getProperty("STOREPWD"));
        map.put("SUBJECT", prop.getProperty("SUBJECT"));
        map.put("licPath", prop.getProperty("licPath"));
        map.put("pubPath", prop.getProperty("pubPath"));
        return map;
    }

    public static void main(String[] args) {
        LicenseInfoDemo infoDemo = new LicenseInfoDemo();
        Map<String,Object> content = infoDemo.info(new LicenseInfoDemoTest().setParam());

    }
}
