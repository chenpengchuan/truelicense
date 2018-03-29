package test;

import de.schlichtherle.demo.LicenseCreateDemo;
import de.schlichtherle.util.PropertiesFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class LicenseCreateDemoTest {
    private String propFilePath = "E:\\github\\truelicense\\src\\main\\config\\create.properties";

    private Map<String,String> setParam() {
        Map<String,String> map = new HashMap<>();
        PropertiesFile propertiesFile = new PropertiesFile(this.propFilePath);
        Properties prop = propertiesFile.Loading();
        map.put("PRIVATEALIAS",prop.getProperty("PRIVATEALIAS")) ;
        map.put("KEYPWD",prop.getProperty("KEYPWD")) ;
        map.put("STOREPWD",prop.getProperty("STOREPWD")) ;
        map.put("SUBJECT",prop.getProperty("SUBJECT")) ;
        map.put("licPath",prop.getProperty("licPath")) ;
        map.put("priPath",prop.getProperty("priPath")) ;
        map.put("issuedTime",prop.getProperty("issuedTime")) ;
        map.put("notBefore",prop.getProperty("notBefore")) ;
        map.put("notAfter",prop.getProperty("notAfter")) ;
        map.put("consumerType",prop.getProperty("consumerType")) ;
        map.put("consumerAmount",prop.getProperty("consumerAmount")) ;
        map.put("info",prop.getProperty("info")) ;
        map.put("sid",prop.getProperty("sid")) ;
        return map;
    }
    public static void main(String[] args) {
        LicenseCreateDemo createDemo = new LicenseCreateDemo();
        createDemo.run(new LicenseCreateDemoTest().setParam());

    }
}
