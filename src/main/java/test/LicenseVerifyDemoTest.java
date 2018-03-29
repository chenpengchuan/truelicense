package test;

import de.schlichtherle.demo.LicenseVerifyDemo;
import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.util.PropertiesFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class LicenseVerifyDemoTest {
    public static String propFilePath = "E:\\project\\truelicense\\license-workdir\\conf\\verify.properties";

    private Map<String, String> setParam() {
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
        LicenseVerifyDemo verifydemo = new LicenseVerifyDemo();
        LicenseContent content = verifydemo.verify(new LicenseVerifyDemoTest().setParam());

    }
}
