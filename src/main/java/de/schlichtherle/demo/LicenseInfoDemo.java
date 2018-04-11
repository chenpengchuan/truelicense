package de.schlichtherle.demo;

import de.schlichtherle.client.LicenseManagerHolder;
import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseManager;
import de.schlichtherle.model.LicenseCheckModel;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LicenseInfoDemo {
    public Map<String,Object> info(Map<String, String> paramers) {
        LicenseContent licenseContent = new LicenseContent();
        Map<String, Object> info = new HashMap<>();
        /************** 证书使用者端执行 ******************/
        VerifyLicenseParams verifyLicenseParams = new VerifyLicenseParams(paramers);
        LicenseManager licenseManager = LicenseManagerHolder.getLicenseManager(verifyLicenseParams.initLicenseParams());
        // 证书信息
        try {
            licenseContent = licenseManager.licenseInfo();
            if (licenseContent != null) {
                info.put("subject", licenseContent.getSubject());
                info.put("notBefore", licenseContent.getNotBefore());
                info.put("notAfter", licenseContent.getNotAfter());
                info.put("info", licenseContent.getInfo());
                info.put("sid", ((LicenseCheckModel) licenseContent.getExtra()).getSid());
                if (licenseContent.getNotAfter().before(new Date())) {
                    info.put("error", "License Expired");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return info;
    }
}
