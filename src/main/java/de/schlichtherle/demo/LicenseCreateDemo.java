package de.schlichtherle.demo;


import de.schlichtherle.client.CreateLicense;
import de.schlichtherle.model.LicenseCheckModel;
import de.schlichtherle.model.LicenseCommonContent;
import de.schlichtherle.model.LicenseCommonParam;

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
//		List<String> macAddressList = new ArrayList<String>();
//		macAddressList.add(mac);
//		licenseCheckModel.setMacAddressList(macAddressList);
        licenseCheckModel.setSid(paramers.get("sid"));
        licenseCommonContent.setLicenseCheckModel(licenseCheckModel);

        Boolean succ = new CreateLicense().create(licenseCommonParam, licenseCommonContent);

        if (succ) {
            System.out.println("create result Successed!");
        } else {
            System.out.println("create result Failed!");
        }

    }


}
