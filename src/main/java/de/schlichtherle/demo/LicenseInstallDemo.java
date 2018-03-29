package de.schlichtherle.demo;

import de.schlichtherle.client.LicenseManagerHolder;
import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseManager;

import java.io.File;
import java.util.Map;

public class LicenseInstallDemo {

    /**
     * 以参数map进行安装，map中包含license.lic文件的文件路径
     * @param paramers
     * @return
     */
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

    /**
     * 以参数map和License.lic的byte数组为参数进行安装
     * @param paramers
     * @param linceseByteArray
     * @return
     */
    public String install(Map<String ,String> paramers,byte[] linceseByteArray){
        LicenseContent licenseContent = new LicenseContent();
        /************** 证书使用者端执行 ******************/
        VerifyLicenseParams verifyLicenseParams = new VerifyLicenseParams(paramers);
        LicenseManager licenseManager = LicenseManagerHolder.getLicenseManager(verifyLicenseParams.initLicenseParams());
        // 安装证书
        try {
            licenseManager.install(linceseByteArray);
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
