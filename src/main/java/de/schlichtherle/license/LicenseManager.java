/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package de.schlichtherle.license;

import de.schlichtherle.model.LicenseCheckModel;
import de.schlichtherle.util.ObfuscatedString;
import de.schlichtherle.xml.GenericCertificate;
import de.schlichtherle.util.ListNets;
import de.schlichtherle.util.SidUtils;

import javax.security.auth.x500.X500Principal;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * This is the top level class which manages all licensing aspects like for
 * instance the creation, installation and verification of license keys.
 * The license manager knows how to install, verify and uninstall full and
 * trial licenses for a given subject and ensures the privacy of the license
 * content in its persistent form (i.e. the <i>license key</i>).
 * For signing, verifying and validating licenses, this class cooperates with
 * a {@link LicenseNotary}.
 * <p>
 * This class is thread-safe.
 *
 * @author  Christian Schlichtherle
 * @version $Id$
 */

public class LicenseManager implements LicenseCreator, LicenseVerifier {
    private static final long TIMEOUT = 1800000L;
    private static final String PREFERENCES_KEY;
    public static final String LICENSE_SUFFIX;
    private static final String PARAM;
    private static final String SUBJECT;
    private static final String KEY_STORE_PARAM;
    private static final String CIPHER_PARAM;
    protected static final String CN;
    private static final String CN_USER;
    private static final String USER;
    private static final String SYSTEM;
    private static final String EXC_INVALID_SUBJECT;
    private static final String EXC_HOLDER_IS_NULL;
    private static final String EXC_ISSUER_IS_NULL;
    private static final String EXC_ISSUED_IS_NULL;
    private static final String EXC_LICENSE_IS_NOT_YET_VALID;
    private static final String EXC_LICENSE_HAS_EXPIRED;
    private static final String EXC_CONSUMER_TYPE_IS_NULL;
    private static final String EXC_CONSUMER_TYPE_IS_NOT_USER;
    private static final String EXC_CONSUMER_AMOUNT_IS_NOT_ONE;
    private static final String EXC_CONSUMER_AMOUNT_IS_NOT_POSITIVE;
    private static final String FILE_FILTER_DESCRIPTION;
    private static final String FILE_FILTER_SUFFIX;
    private LicenseParam param;
    private LicenseNotary notary;
    private PrivacyGuard guard;
    private GenericCertificate certificate;
    private long certificateTime;
    private FileFilter fileFilter;
    private Preferences preferences;

    protected static final Date midnight() {
        Calendar var0 = Calendar.getInstance();
        var0.set(11, 0);
        var0.set(12, 0);
        var0.set(13, 0);
        var0.set(14, 0);
        return var0.getTime();
    }

    protected LicenseManager() {
    }

    public LicenseManager(LicenseParam var1) throws NullPointerException, IllegalPasswordException {
        this.setLicenseParam(var1);
    }

    public LicenseParam getLicenseParam() {
        return this.param;
    }

    public synchronized void setLicenseParam(LicenseParam var1) throws NullPointerException, IllegalPasswordException {
        if (var1 == null) {
            throw new NullPointerException(PARAM);
        } else if (var1.getSubject() == null) {
            throw new NullPointerException(SUBJECT);
        } else if (var1.getKeyStoreParam() == null) {
            throw new NullPointerException(KEY_STORE_PARAM);
        } else {
            CipherParam var2 = var1.getCipherParam();
            if (var2 == null) {
                throw new NullPointerException(CIPHER_PARAM);
            } else {
                Policy.getCurrent().checkPwd(var2.getKeyPwd());
                this.param = var1;
                this.notary = null;
                this.certificate = null;
                this.certificateTime = 0L;
                this.fileFilter = null;
                this.preferences = null;
            }
        }
    }

    public final synchronized void store(LicenseContent var1, File var2) throws Exception {
        this.store(var1, this.getLicenseNotary(), var2);
    }

    /** @deprecated */
    protected synchronized void store(LicenseContent var1, LicenseNotary var2, File var3) throws Exception {
        storeLicenseKey(this.create(var1, var2), var3);
    }

    public final synchronized byte[] create(LicenseContent var1) throws Exception {
        return this.create(var1, this.getLicenseNotary());
    }

    /** @deprecated */
    protected synchronized byte[] create(LicenseContent var1, LicenseNotary var2) throws Exception {
        this.initialize(var1);
        this.validate(var1);
        GenericCertificate var3 = var2.sign(var1);
        byte[] var4 = this.getPrivacyGuard().cert2key(var3);
        return var4;
    }

    public final synchronized LicenseContent install(File var1) throws Exception {
        return this.install(var1, this.getLicenseNotary());
    }

    /** @deprecated */
    protected synchronized LicenseContent install(File var1, LicenseNotary var2) throws Exception {
        return this.install(loadLicenseKey(var1), var2);
    }

    /** @deprecated */
    protected synchronized LicenseContent install(byte[] var1, LicenseNotary var2) throws Exception {
        GenericCertificate var3 = this.getPrivacyGuard().key2cert(var1);
        var2.verify(var3);
        LicenseContent var4 = (LicenseContent)var3.getContent();
        this.validate(var4);
        this.verifyValidate(var4);
        this.setLicenseKey(var1);
        this.setCertificate(var3);
        return var4;
    }
    public final synchronized LicenseContent install(byte[] licenseByte) throws Exception {
        LicenseNotary licenseNotary = this.getLicenseNotary();
        GenericCertificate var3 = this.getPrivacyGuard().key2cert(licenseByte);
        licenseNotary.verify(var3);
        LicenseContent var4 = (LicenseContent)var3.getContent();
        this.validate(var4);
        this.verifyValidate(var4);
        this.setLicenseKey(licenseByte);
        this.setCertificate(var3);
        return var4;
    }
    public final synchronized LicenseContent verify() throws Exception {
        return this.verify(this.getLicenseNotary());
    }

    /** @deprecated */
    protected synchronized LicenseContent verify(LicenseNotary var1) throws Exception {
        GenericCertificate var2 = this.getCertificate();
        if (var2 != null) {
            return (LicenseContent)var2.getContent();
        } else {
            byte[] var3 = this.getLicenseKey();
            if (var3 == null) {
                throw new NoLicenseInstalledException(this.getLicenseParam().getSubject());
            } else {
                var2 = this.getPrivacyGuard().key2cert(var3);
                var1.verify(var2);
                LicenseContent var4 = (LicenseContent)var2.getContent();
                this.verifyValidate(var4);
                this.validate(var4);
                this.setCertificate(var2);
                return var4;
            }
        }
    }

    public final synchronized LicenseContent verify(byte[] var1) throws Exception {
        return this.verify(var1, this.getLicenseNotary());
    }

    /** @deprecated */
    protected synchronized LicenseContent verify(byte[] var1, LicenseNotary var2) throws Exception {
        GenericCertificate var3 = this.getPrivacyGuard().key2cert(var1);
        var2.verify(var3);
        LicenseContent var4 = (LicenseContent)var3.getContent();
        this.verifyValidate(var4);
        this.validate(var4);
        return var4;
    }

    public synchronized void uninstall() throws Exception {
        this.setLicenseKey((byte[])null);
        this.setCertificate((GenericCertificate)null);
    }

    protected synchronized void initialize(LicenseContent var1) {
        if (var1.getHolder() == null) {
            var1.setHolder(new X500Principal(CN_USER));
        }

        if (var1.getSubject() == null) {
            var1.setSubject(this.getLicenseParam().getSubject());
        }

        if (var1.getConsumerType() == null) {
            Preferences var2 = this.getLicenseParam().getPreferences();
            if (var2 != null) {
                if (var2.isUserNode()) {
                    var1.setConsumerType(USER);
                } else {
                    var1.setConsumerType(SYSTEM);
                }

                var1.setConsumerAmount(1);
            }
        }

        if (var1.getIssuer() == null) {
            var1.setIssuer(new X500Principal(CN + this.getLicenseParam().getSubject()));
        }

        if (var1.getIssued() == null) {
            var1.setIssued(new Date());
        }

        if (var1.getNotBefore() == null) {
            var1.setNotBefore(midnight());
        }

    }

    protected synchronized void validate(LicenseContent var1) throws LicenseContentException {
//        String macAddress = var1.getExtra().toString();
//        try {
//            if (!ListNets.validateMacAddress(macAddress)) {
//                throw new LicenseContentException(EXC_LICENSE_HAS_EXPIRED);
//            }
//        } catch (SocketException e) {
//            // TODO Auto-generated catch block
//            throw new LicenseContentException(EXC_LICENSE_HAS_EXPIRED);
//        }
        LicenseParam var2 = this.getLicenseParam();
        if (!var2.getSubject().equals(var1.getSubject())) {
            throw new LicenseContentException(EXC_INVALID_SUBJECT);
        } else if (var1.getHolder() == null) {
            throw new LicenseContentException(EXC_HOLDER_IS_NULL);
        } else if (var1.getIssuer() == null) {
            throw new LicenseContentException(EXC_ISSUER_IS_NULL);
        } else if (var1.getIssued() == null) {
            throw new LicenseContentException(EXC_ISSUED_IS_NULL);
        } else {
            Date var3 = new Date();
            Date var4 = var1.getNotBefore();
            if (var4 != null && var3.before(var4)) {
                throw new LicenseContentException(EXC_LICENSE_IS_NOT_YET_VALID);
            } else {
                Date var5 = var1.getNotAfter();
                if (var5 != null && var3.after(var5)) {
                    throw new LicenseContentException(EXC_LICENSE_HAS_EXPIRED);
                } else {
                    String var6 = var1.getConsumerType();
                    if (var6 == null) {
                        throw new LicenseContentException(EXC_CONSUMER_TYPE_IS_NULL);
                    } else {
                        Preferences var7 = var2.getPreferences();
                        if (var7 != null && var7.isUserNode()) {
                            if (!USER.equalsIgnoreCase(var6)) {
                                throw new LicenseContentException(EXC_CONSUMER_TYPE_IS_NOT_USER);
                            }

                            if (var1.getConsumerAmount() != 1) {
                                throw new LicenseContentException(EXC_CONSUMER_AMOUNT_IS_NOT_ONE);
                            }
                        } else if (var1.getConsumerAmount() <= 0) {
                            throw new LicenseContentException(EXC_CONSUMER_AMOUNT_IS_NOT_POSITIVE);
                        }

                    }
                }
            }
        }
    }
    /**
     * 添加EXTA属性校验，比如多MAC绑定
     * @param content
     * @throws LicenseContentException
     */
    protected synchronized void verifyValidate(final LicenseContent content)
            throws LicenseContentException {
//        validate(content);
        //add validate mac Lists check
        LicenseCheckModel licenseCheckModel = (LicenseCheckModel)content.getExtra();
        if(null != licenseCheckModel){
            List<String> macAddressList = licenseCheckModel.getMacAddressList();
            if(null != macAddressList && macAddressList.size() > 0){
                try {
                    boolean validate = false;
                    for (String macAddress : macAddressList) {
                        if (ListNets.validateMacAddress(macAddress)) {
                            validate = true;
                            break;
                        }
                    }
                    if(!validate){
                        throw new LicenseContentException(EXC_LICENSE_HAS_EXPIRED);
                    }
                } catch (SocketException e) {
                    throw new LicenseContentException(EXC_LICENSE_HAS_EXPIRED);
                }
            }
            String lisenseSid = licenseCheckModel.getSid();
            if(lisenseSid != null){
                String computerSid = SidUtils.getSid();
                boolean validate = false;
                if(lisenseSid.equals(computerSid)){
                    validate = true;
                }
                if(!validate){
                    System.err.println("the license is error please take your sid contact your administrator !");
                    throw new LicenseContentException(EXC_LICENSE_HAS_EXPIRED);
                }
            }
        }
    }
    /** @deprecated */
    protected GenericCertificate getCertificate() {
        return this.certificate != null && System.currentTimeMillis() < this.certificateTime + 1800000L ? this.certificate : null;
    }

    /** @deprecated */
    protected synchronized void setCertificate(GenericCertificate var1) {
        this.certificate = var1;
        this.certificateTime = System.currentTimeMillis();
    }

    /** @deprecated */
    protected byte[] getLicenseKey() {
        return this.getLicenseParam().getPreferences().getByteArray(PREFERENCES_KEY, (byte[])null);
    }

    /** @deprecated */
    protected synchronized void setLicenseKey(byte[] var1) {
        Preferences var2 = this.getLicenseParam().getPreferences();
        if (var1 != null) {
            var2.putByteArray(PREFERENCES_KEY, var1);
        } else {
            var2.remove(PREFERENCES_KEY);
        }

    }

    protected static void storeLicenseKey(byte[] var0, File var1) throws IOException {
        FileOutputStream var2 = new FileOutputStream(var1);

        try {
            var2.write(var0);
        } finally {
            try {
                var2.close();
            } catch (IOException var9) {
                ;
            }

        }

    }

    protected static byte[] loadLicenseKey(File var0) throws IOException {
        int var1 = Math.min((int)var0.length(), 1048576);
        FileInputStream var2 = new FileInputStream(var0);
        byte[] var3 = new byte[var1];

        try {
            var2.read(var3);
        } finally {
            try {
                var2.close();
            } catch (IOException var10) {
                ;
            }

        }

        return var3;
    }

    protected synchronized LicenseNotary getLicenseNotary() {
        if (this.notary == null) {
            this.notary = new LicenseNotary(this.getLicenseParam().getKeyStoreParam());
        }

        return this.notary;
    }

    protected synchronized PrivacyGuard getPrivacyGuard() {
        if (this.guard == null) {
            this.guard = new PrivacyGuard(this.getLicenseParam().getCipherParam());
        }

        return this.guard;
    }

    public synchronized FileFilter getFileFilter() {
        if (this.fileFilter != null) {
            return this.fileFilter;
        } else {
            final String var1 = Resources.getString(FILE_FILTER_DESCRIPTION, this.getLicenseParam().getSubject());
            if (File.separatorChar == '\\') {
                this.fileFilter = new FileFilter() {
                    public boolean accept(File var1x) {
                        return var1x.isDirectory() || var1x.getPath().toLowerCase().endsWith(LicenseManager.LICENSE_SUFFIX);
                    }

                    public String getDescription() {
                        return var1 + LicenseManager.FILE_FILTER_SUFFIX;
                    }
                };
            } else {
                this.fileFilter = new FileFilter() {
                    public boolean accept(File var1x) {
                        return var1x.isDirectory() || var1x.getPath().endsWith(LicenseManager.LICENSE_SUFFIX);
                    }

                    public String getDescription() {
                        return var1 + LicenseManager.FILE_FILTER_SUFFIX;
                    }
                };
            }

            return this.fileFilter;
        }
    }

    static {
//        $assertionsDisabled = !LicenseManager.class.desiredAssertionStatus();
        PREFERENCES_KEY = (new ObfuscatedString(new long[]{-2999492566024573771L, -1728025856628382701L})).toString();
        LICENSE_SUFFIX = (new ObfuscatedString(new long[]{-7559156485370438418L, 5084921010819724770L})).toString();
        if (!LICENSE_SUFFIX.equals(LICENSE_SUFFIX.toLowerCase())) {
            throw new AssertionError();
        } else {
            PARAM = LicenseNotary.PARAM;
            SUBJECT = (new ObfuscatedString(new long[]{-6788193907359448604L, -2787711522493615434L})).toString();
            KEY_STORE_PARAM = (new ObfuscatedString(new long[]{4943981370588954830L, 8065447823433585419L, -2749528823549501332L})).toString();
            CIPHER_PARAM = (new ObfuscatedString(new long[]{-3651048337721043740L, 1928803483347080380L, 1649789960289346230L})).toString();
            CN = (new ObfuscatedString(new long[]{7165044359350484836L, -6008675436704023088L})).toString();
            CN_USER = CN + Resources.getString((new ObfuscatedString(new long[]{-883182015789302099L, 6587252612286394632L})).toString());
            USER = (new ObfuscatedString(new long[]{-6950934198262740461L, -10280221617836935L})).toString();
            SYSTEM = (new ObfuscatedString(new long[]{-1441033263392531498L, 6113162389128247115L})).toString();
            EXC_INVALID_SUBJECT = (new ObfuscatedString(new long[]{-9211605111142713620L, 391714365510707393L, -7356761750428556372L, 6379560902598103028L})).toString();
            EXC_HOLDER_IS_NULL = (new ObfuscatedString(new long[]{7150026245468079143L, 6314884536402738366L, -1360923923476698800L})).toString();
            EXC_ISSUER_IS_NULL = (new ObfuscatedString(new long[]{-3034693013076752554L, -1011266899694033610L, 6775785917404597234L})).toString();
            EXC_ISSUED_IS_NULL = (new ObfuscatedString(new long[]{-6084371209004858580L, 3028840747031697166L, -3524637886726219307L})).toString();
            EXC_LICENSE_IS_NOT_YET_VALID = (new ObfuscatedString(new long[]{5434633639502011825L, -3406117476263181371L, 6903673940810780388L, -6816911225052310716L})).toString();
            EXC_LICENSE_HAS_EXPIRED = (new ObfuscatedString(new long[]{1000558500458715757L, -6998261911041258483L, -5490039629745846648L, 3561172928787106880L})).toString();
            EXC_CONSUMER_TYPE_IS_NULL = (new ObfuscatedString(new long[]{-3274088377466921882L, -1704115158449736962L, -1134622897105293263L, 2875630655915253859L})).toString();
            EXC_CONSUMER_TYPE_IS_NOT_USER = (new ObfuscatedString(new long[]{-3559580260061340089L, 8807812719464926891L, 3255622466169980128L, 3208430498260873670L, 8772089725159421213L})).toString();
            EXC_CONSUMER_AMOUNT_IS_NOT_ONE = (new ObfuscatedString(new long[]{6854702630454082314L, -1676630527348424687L, 4853969635229547239L, -7087814313396201500L, 7133601245775504376L})).toString();
            EXC_CONSUMER_AMOUNT_IS_NOT_POSITIVE = (new ObfuscatedString(new long[]{-5670394608177286583L, -3674104453170648872L, 4159301984262248157L, 7442355638167795990L, 4780252201915657674L})).toString();
            FILE_FILTER_DESCRIPTION = (new ObfuscatedString(new long[]{3160933239845492228L, -2320904495012387647L, -5935185636215549881L, -3418607682842311949L})).toString();
            FILE_FILTER_SUFFIX = (new ObfuscatedString(new long[]{-6576160320308571504L, 7010427383913371869L})).toString();
        }
    }
}
