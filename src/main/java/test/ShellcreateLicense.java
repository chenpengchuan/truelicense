package test;

import de.schlichtherle.demo.LicenseCreateDemo;
import de.schlichtherle.util.PropertiesFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ShellcreateLicense {
    private static String propFilePath = "E:\\license-conf-dir\\create.properties";

    private Map<String, String> setParam(String filePath) throws IOException {
        String issuedTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        Properties prop = new PropertiesFile(filePath).Loading();
        String LICENSE_PATH = prop.getProperty("LICENSE_PATH");
        if (LICENSE_PATH.lastIndexOf("/") < LICENSE_PATH.length() || LICENSE_PATH.lastIndexOf("\\") < LICENSE_PATH.length()) {
            LICENSE_PATH = LICENSE_PATH + "/";
        }
        LICENSE_PATH = LICENSE_PATH + prop.getProperty("COMPANY") + "/"+new SimpleDateFormat("yyyyMMddHHmm").format(new Date())+"/";
        Map<String, String> map = new HashMap<String, String>();
        map.put("COMPANY", prop.getProperty("COMPANY"));
        map.put("LICENSE_PATH", LICENSE_PATH);
        map.put("licPath", LICENSE_PATH + prop.getProperty("licPath"));
        map.put("hexPath", LICENSE_PATH + prop.getProperty("hexPath"));
        map.put("issuedTime", issuedTime);
        map.put("PRIVATEALIAS", prop.getProperty("PRIVATEALIAS"));
        map.put("KEYPWD", prop.getProperty("KEYPWD"));
        map.put("STOREPWD", prop.getProperty("STOREPWD"));
        map.put("SUBJECT", prop.getProperty("SUBJECT"));
        map.put("priPath", prop.getProperty("priPath"));
        map.put("notBefore", prop.getProperty("notBefore"));
        map.put("notAfter", prop.getProperty("notAfter"));
        map.put("consumerType", prop.getProperty("consumerType"));
        map.put("consumerAmount", prop.getProperty("consumerAmount"));
        map.put("info", prop.getProperty("info"));
        map.put("sid", prop.getProperty("sid"));
        return map;
    }

    private void setReadme(Map<String, String> map) throws IOException {
        String path = map.get("LICENSE_PATH") + "README.txt";
        File file = new File(path);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        } else {
            file.delete();
        }
        file.createNewFile();
        FileWriter writer = new FileWriter(path, true);
        writer.write("COMPANY : " + map.get("COMPANY") + "\r\n");
        writer.write("info : " + map.get("info") + "\r\n");
        writer.write("sid : " + map.get("sid") + "\r\n");
        writer.write("createTime : " + map.get("issuedTime") + "\r\n");
        writer.write("notBefore : " + map.get("notBefore") + "\r\n");
        writer.write("notAfter : " + map.get("notAfter") + "\r\n");
        writer.close();

    }

    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            ShellcreateLicense create = new ShellcreateLicense();
            LicenseCreateDemo createDemo = new LicenseCreateDemo();
            Map<String, String> map = new ShellcreateLicense().setParam(args[0]);
            create.setReadme(map);
            createDemo.run(map);
        } else {
            System.err.println("can not found parameter create.properties!");
        }

    }
}
