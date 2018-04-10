package de.schlichtherle.demo;

import de.schlichtherle.client.LicenseManagerHolder;
import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseManager;

import java.util.Map;

public class LicenseInfoDemo {
    public LicenseContent verify(Map<String, String> paramers) {
        LicenseContent licenseContent = new LicenseContent();
        /************** 证书使用者端执行 ******************/
        VerifyLicenseParams verifyLicenseParams = new VerifyLicenseParams(paramers);
        LicenseManager licenseManager = LicenseManagerHolder.getLicenseManager(verifyLicenseParams.initLicenseParams());
        // 证书信息
        try {
            licenseContent = licenseManager.licenseInfo();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return licenseContent;
    }
}
