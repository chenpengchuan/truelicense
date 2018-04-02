package test;

import de.schlichtherle.demo.LicenseCreateDemo;
import de.schlichtherle.util.PropertiesFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class LicenseCreateDemoTest {
    private static String propFilePath = "E:\\project\\truelicense\\src\\main\\config\\create.properties";

    private Map<String, String> setParam(String filePath) {
        String issuedTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        Map<String, String> map = new HashMap<>();
        Properties prop = new PropertiesFile(filePath).Loading();
        map.put("PRIVATEALIAS", prop.getProperty("PRIVATEALIAS"));
        map.put("KEYPWD", prop.getProperty("KEYPWD"));
        map.put("STOREPWD", prop.getProperty("STOREPWD"));
        map.put("SUBJECT", prop.getProperty("SUBJECT"));
        map.put("licPath", prop.getProperty("licPath"));
        map.put("priPath", prop.getProperty("priPath"));
        map.put("issuedTime", issuedTime);
        map.put("notBefore", prop.getProperty("notBefore"));
        map.put("notAfter", prop.getProperty("notAfter"));
        map.put("consumerType", prop.getProperty("consumerType"));
        map.put("consumerAmount", prop.getProperty("consumerAmount"));
        map.put("info", prop.getProperty("info"));
        map.put("sid", prop.getProperty("sid"));
        String s = prop.getProperty("hexPath");
        map.put("hexPath", prop.getProperty("hexPath"));
        return map;
    }

    public static void main(String[] args) {
        LicenseCreateDemo createDemo = new LicenseCreateDemo();
        if (args.length > 0) {
            createDemo.run(new LicenseCreateDemoTest().setParam(args[0]));
        } else {
            createDemo.run(new LicenseCreateDemoTest().setParam(propFilePath));
        }

    }
}
