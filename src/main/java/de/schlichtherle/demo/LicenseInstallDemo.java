package de.schlichtherle.demo;

import de.schlichtherle.client.LicenseManagerHolder;
import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseManager;

import java.io.File;
import java.util.Map;

public class LicenseInstallDemo {

    public String install(Map<String, String> paramers) {
        LicenseContent licenseContent = new LicenseContent();
        /************** 证书使用者端执行 ******************/
        VerifyLicenseParams verifyLicenseParams = new VerifyLicenseParams(paramers);
        LicenseManager licenseManager = LicenseManagerHolder.getLicenseManager(verifyLicenseParams.initLicenseParams());
        // 安装证书
        try {
            licenseManager.install(new File(paramers.get("licPath")));
            System.out.println("license install suceessd!");
            return "SUCCESS";
        } catch (Exception e) {
            e.printStackTrace();
            String err = e.toString();
            if (err.lastIndexOf("Exception:") >= 0) {
                err = err.substring(err.indexOf("Exception:") + 11);
            } else {
                err = "license install failed!";
            }
            return err;
        }
    }
}
