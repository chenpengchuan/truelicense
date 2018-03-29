package de.schlichtherle.demo;

import de.schlichtherle.client.VerifyLicense;
import de.schlichtherle.license.*;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

public class VerifyLicenseParams {
    private Map<String, String> params = new HashMap<>();

    public VerifyLicenseParams(Map<String, String> map) {
        this.params = map;
    }

    // 返回验证证书需要的参数
    public LicenseParam initLicenseParams() {
        Preferences preference = Preferences.userNodeForPackage(VerifyLicense.class);
        CipherParam cipherParam = new DefaultCipherParam(params.get("STOREPWD"));

        KeyStoreParam privateStoreParam = new DefaultKeyStoreParam(VerifyLicense.class, params.get("pubPath"), params.get("PUBLICALIAS"), params.get("STOREPWD"),
                null);
        LicenseParam licenseParams = new DefaultLicenseParam(params.get("SUBJECT"), preference, privateStoreParam, cipherParam);
        return licenseParams;
    }
}
