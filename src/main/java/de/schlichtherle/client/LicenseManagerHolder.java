package de.schlichtherle.client;
import de.schlichtherle.license.LicenseManager;
import de.schlichtherle.license.LicenseParam;

/**
 * LicenseManager
 * @author wucy
 */
public class LicenseManagerHolder {

//	private static  LicenseManager licenseManager;

	public static synchronized LicenseManager getLicenseManager(LicenseParam licenseParams) {
		LicenseManager licenseManager = new LicenseManager(licenseParams);
    	return licenseManager;
    }
}