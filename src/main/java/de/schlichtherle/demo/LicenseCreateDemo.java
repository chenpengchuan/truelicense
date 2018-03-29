package de.schlichtherle.demo;


import de.schlichtherle.client.CreateLicense;
import de.schlichtherle.model.LicenseCheckModel;
import de.schlichtherle.model.LicenseCommonContent;
import de.schlichtherle.model.LicenseCommonParam;
import de.schlichtherle.util.LicFileutils;

import java.io.IOException;
import java.util.Map;

/**
 * 创建LicenseDemo
 */
public class LicenseCreateDemo {

    public void run(Map<String, String> paramers) {

        LicenseCommonParam licenseCommonParam = new LicenseCommonParam();
        licenseCommonParam.setAlias(paramers.get("PRIVATEALIAS"));
        licenseCommonParam.setResource(paramers.get("priPath"));
        licenseCommonParam.setStorePwd(paramers.get("STOREPWD"));
        licenseCommonParam.setKeyPwd(paramers.get("KEYPWD"));
        licenseCommonParam.setSubject(paramers.get("SUBJECT"));
        licenseCommonParam.setLicPath(paramers.get("licPath"));

        LicenseCommonContent licenseCommonContent = new LicenseCommonContent();
        licenseCommonContent.setIssuedTime(paramers.get("issuedTime"));
        licenseCommonContent.setNotBefore(paramers.get("notBefore"));
        licenseCommonContent.setNotAfter(paramers.get("notAfter"));
        licenseCommonContent.setConsumerType(paramers.get("consumerType"));
        licenseCommonContent.setConsumerAmount(1);
        licenseCommonContent.setInfo(paramers.get("info"));

        LicenseCheckModel licenseCheckModel = new LicenseCheckModel();
        licenseCheckModel.setSid(paramers.get("sid"));
        licenseCommonContent.setLicenseCheckModel(licenseCheckModel);

        Boolean succ = new CreateLicense().create(licenseCommonParam, licenseCommonContent);

        if (succ) {
            try {
                LicFileutils.byteFilleToHexFile(paramers.get("licPath"),paramers.get("hexPath"));
                System.out.println("create license Successed!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("create license Failed!");
        }

    }


}
