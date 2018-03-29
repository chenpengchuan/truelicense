package de.schlichtherle.demo;

import de.schlichtherle.client.LicenseManagerHolder;
import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseManager;
import sun.misc.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

/**
 * 验证licenseDemo
 */
public class LicenseVerifyDemo {

    public LicenseContent verify(Map<String, String> paramers) {
        LicenseContent licenseContent = new LicenseContent();
        /************** 证书使用者端执行 ******************/
        VerifyLicenseParams verifyLicenseParams = new VerifyLicenseParams(paramers);
        LicenseManager licenseManager = LicenseManagerHolder.getLicenseManager(verifyLicenseParams.initLicenseParams());
//        // 安装证书
//        try {
//            licenseManager.install(new File(paramers.get("licPath")));
//            System.out.println("license install suceessd!");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
        // 验证证书
        try {
            licenseContent = licenseManager.verify();
            System.out.println("license verify successd!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("license verify failde!");
            return null;
        }
        return licenseContent;
    }

}
