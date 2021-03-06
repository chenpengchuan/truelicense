/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package de.schlichtherle.license;

import de.schlichtherle.util.ObfuscatedString;
import de.schlichtherle.xml.GenericCertificate;
import de.schlichtherle.xml.PersistenceService;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

/**
 * This notary knows how to sign and verify a {@link GenericCertificate}.
 * <p>
 * This class is <em>not</em> thread safe.
 *
 * @author  Christian Schlichtherle
 * @version $Id$
 */
public class LicenseNotary {
    
    /** The buffer size for I/O. */
    private static final int BUFSIZE = 5 * 1024;

    /** => "param" */
    static final String PARAM = new ObfuscatedString(new long[] {
        0x9462FFDE0183752L, 0xE2A34A222A24DB14L
    }).toString();

    /** => "alias" */
    private static final String ALIAS = new ObfuscatedString(new long[] {
        0xF7122BB1103EE24L, 0x5D073BF77D50CE8AL
    }).toString();

    /** => "exc.noKeyPwd" */
    private static final String EXC_NO_KEY_PWD = new ObfuscatedString(new long[] {
        0x9BEEC1A930D89BC1L, 0x314F8BFE96B1D7BL, 0x7D41D459E6191D0AL
    }).toString();

    /** => "exc.noKeyEntry" */
    private static final String EXC_NO_KEY_ENTRY = new ObfuscatedString(new long[] {
        0xECC3EE809CC45994L, 0x395EC0314F8227A1L, 0x90B1DBA3D701F0FBL
    }).toString();

    /** => "exc.privateKeyOrPwdIsNotAllowed" */
    private static final String EXC_PRIVATE_KEY_OR_PWD_IS_NOT_ALLOWED = new ObfuscatedString(new long[] {
        0xD6E9FE0BD39F8075L, 0x351D278C14FABB1AL, 0xD64A9C9BD412AB10L,
        0x1AEB0F657DB66448L, 0x41EE587D2CD73A1AL
    }).toString();

    /** => "exc.noCertificateEntry" */
    private static final String EXC_NO_CERTIFICATE_ENTRY = new ObfuscatedString(new long[] {
        0xCA437064C1D0C41EL, 0xDDBBA0FF1F17FC35L, 0x5D2CD0D970444C3DL,
        0xF94EAAC3F634D04CL
    }).toString();

    /** => "SHA1withDSA" */
    private static final String SHA1_WITH_DSA = new ObfuscatedString(new long[] {
        0xEB0CFFD676FD2839L, 0x176DF514D5A0ED59L, 0xBFE1DE24AEF8E9B0L
    }).toString();

    /** => "JKS" */
    private static final String JKS = new ObfuscatedString(new long[] {
        0xA97AF8FB6356CB08L, 0x20E47C2995D2FE7AL
    }).toString();

    private KeyStoreParam param; // init by setKeyStoreParam() - should be accessed via getKeyStoreParam() only!

    private KeyStore keyStore; // init by getKeyStore()
    private PrivateKey privateKey; // lazy initialised by getPrivateKey()
    private PublicKey  publicKey;  // lazy initialised by getPublicKey()

    /**
     * Creates a new License Notary.
     * <p>
     * <b>Warning:</b> The notary created by this constructor is <em>not</em>
     * valid and cannot be used unless {@link #setKeyStoreParam(KeyStoreParam)}
     * is called!
     */
    protected LicenseNotary() {
    }

    /**
     * 
     * Creates a new License Notary.
     * 
     * @param  param the keyStore configuration parameters
     *         - may <em>not</em> be {@code null}.
     * @throws NullPointerException if the given parameter object does not
     *         obey the contract of its interface due to a {@code null}
     *         pointer.
     * @throws IllegalPasswordException if any password in the parameter object
     *         does not comply to the current policy.
     */
    public LicenseNotary(final KeyStoreParam param) {
        setKeyStoreParam0(param);
    }

    /** Returns the keyStore configuration parameters. */
    public KeyStoreParam getKeyStoreParam() {
        return param;
    }

    /**
     * Sets the keyStore configuration parameters.
     * Calling this method resets the notary as if it had just been created.
     * 
     * @param  param the keyStore configuration parameters
     *         - may <em>not</em> be {@code null}.
     * @throws NullPointerException if the given parameter object does not
     *         obey the contract of its interface due to a {@code null}
     *         pointer.
     * @throws IllegalPasswordException if any password in the parameter object
     *         does not comply to the current policy.
     */
    public void setKeyStoreParam(final KeyStoreParam param) {
        setKeyStoreParam0(param);
    }

    private void setKeyStoreParam0(final KeyStoreParam param) {
        // Check parameters to implement fail-fast behaviour and enforce
        // a reasonably good security level.
        if (param == null)
            throw new NullPointerException(PARAM);
        if (param.getAlias() == null)
            throw new NullPointerException(ALIAS);
        final Policy policy = Policy.getCurrent();
        final String storePwd = param.getStorePwd();
        policy.checkPwd(storePwd);
        final String keyPwd = param.getKeyPwd();
        if (keyPwd != null)
            policy.checkPwd(keyPwd);

        this.param = param;
        keyStore = null;
        privateKey = null;
        publicKey = null;
    }

    /**
     * Encodes and signs the given {@code content} and returns a locked
     * generic certificate holding the encoded content and its digital
     * signature.
     * <p>
     * Please note the following:
     * <ul>
     * <li>Because this method locks the certificate, a subsequent call to
     *     {@link #sign(GenericCertificate, Object)} or
     *     {@link #verify(GenericCertificate)} is redundant
     *     and will throw a {@code PropertyVetoException}.
     *     Use {@link GenericCertificate#isLocked()} to detect whether a
     *     generic certificate has been successfuly signed or verified before
     *     or call {@link GenericCertificate#getContent()} and expect an 
     *     Exception to be thrown if it hasn't.</li>
     * <li>There is no way to unlock the returned certificate.
     *     Call the copy constructor of {@link GenericCertificate} if you
     *     need an unlocked copy of the certificate.</li>
     * </ul>
     *
     * @param  content the object to sign. This must either be a JavaBean or an
     *         instance of any other class which is supported by
     *         {@link PersistenceService}
     *         - maybe {@code null}.
     * @return A locked generic certificate holding the encoded content and
     *         its digital signature.
     * @throws Exception for various reasons.
     */
    public GenericCertificate sign(Object content) throws Exception {
        GenericCertificate cert = new GenericCertificate();
        sign(cert, content);
        return cert;
    }

    /**
     * Encodes and signs the given {@code content} in the given
     * {@code certificate} and locks it.
     * <p>
     * Please note the following:
     * <ul>
     * <li>This method will throw a {@code PropertyVetoException} if the
     *     certificate is already locked, i.e. if it has been signed or
     *     verified before.</li>
     * <li>Because this method locks the certificate, a subsequent call to
     *     {@link #sign(GenericCertificate, Object)} or
     *     {@link #verify(GenericCertificate)} is redundant
     *     and will throw a {@code PropertyVetoException}.
     *     Use {@link GenericCertificate#isLocked()} to detect whether a
     *     generic certificate has been successfuly signed or verified before
     *     or call {@link GenericCertificate#getContent()} and expect an 
     *     Exception to be thrown if it hasn't.</li>
     * <li>There is no way to unlock the certificate.
     *     Call the copy constructor of {@link GenericCertificate} if you
     *     need an unlocked copy of the certificate.</li>
     * </ul>
     *
     * @param certificate the generic certificate used to hold the encoded
     *        content and its digital signature.
     * @param content the object to sign. This must either be a JavaBean or an
     *        instance of any other class which is supported by
     *        {@code {@link PersistenceService }}
     *        - maybe {@code null}.
     * @throws Exception for various reasons.
     */
    void sign(GenericCertificate certificate, Object content) throws Exception {
        certificate.sign(content, getPrivateKey(), getSignatureEngine());
    }

    /** 
     * Verifies the digital signature of the encoded content in the given
     * {@code certificate} and locks it.
     * <p>
     * Please note the following:
     * <ul>
     * <li>This method will throw a {@code PropertyVetoException} if the
     *     certificate is already locked, i.e. if it has been signed or
     *     verified before.</li>
     * <li>Because this method locks the certificate, a subsequent call to
     *     {@link #sign(GenericCertificate, Object)} or
     *     {@link #verify(GenericCertificate)} is redundant
     *     and will throw a {@code PropertyVetoException}.
     *     Use {@link GenericCertificate#isLocked()} to detect whether a
     *     generic certificate has been successfuly signed or verified before
     *     or call {@link GenericCertificate#getContent()} and expect an 
     *     Exception to be thrown if it hasn't.</li>
     * <li>There is no way to unlock the certificate.
     *     Call the copy constructor of {@link GenericCertificate} if you
     *     need an unlocked copy of the certificate.</li>
     * </ul>
     *
     * @param certificate the generic certificate to verify
     *        - may <em>not</em> be {@code null}.
     * @throws Exception a subclass of this class may be thrown for various
     *         reasons.
     */
    public void verify(GenericCertificate certificate) throws Exception {
        certificate.verify(getPublicKey(), getSignatureEngine());
    }

    /**
     * Returns the private key from the keyStore.
     * 
     * @throws LicenseNotaryException if the parameters used to access the
     *         corresponding key store are insufficient or incorrect.
     *         Note that you should always use
     *         {@link Throwable#getLocalizedMessage()} to get a (possibly
     *         localized) meaningful detail message.
     * @throws IOException if there is an I/O or format problem with the
     *         keyStore data.
     * @throws CertificateException if any of the certificates in the
     *         keyStore could not be loaded.
     * @throws NoSuchAlgorithmException if the algorithm used to check
     *         the integrity of the keyStore cannot be found.
     * @throws UnrecoverableKeyException if the key cannot get recovered
     *         (e.g. the given password is wrong).
     */
    protected PrivateKey getPrivateKey() throws  LicenseNotaryException,
            IOException,
            CertificateException,
            NoSuchAlgorithmException,
            UnrecoverableKeyException {
        if (privateKey == null) {
            final KeyStoreParam param = getKeyStoreParam();
            final String keyPwd = param.getKeyPwd();
            final String alias = param.getAlias();
            if (keyPwd == null)
                throw new LicenseNotaryException(EXC_NO_KEY_PWD, alias);
            final KeyStore keystore = getKeyStore();
            try {
                privateKey = (PrivateKey) keystore.getKey(
                        alias, keyPwd.toCharArray());
            } catch (KeyStoreException keystoreIsAlreadyLoaded) {
                throw new AssertionError(keystoreIsAlreadyLoaded);
            }
            if (privateKey == null)
                throw new LicenseNotaryException(EXC_NO_KEY_ENTRY, alias);
        }

        return privateKey;
    }

    /**
     * Returns the public key from the keyStore
     * 
     * @throws LicenseNotaryException if the parameters used to access the
     *         corresponding key store are insufficient or incorrect.
     *         Note that you should always use
     *         {@link Throwable#getLocalizedMessage()} to get a (possibly
     *         localized) meaningful detail message.
     * @throws IOException if there is an I/O or format problem with the
     *         keyStore data.
     * @throws CertificateException if any of the certificates in the
     *         keyStore could not be loaded.
     * @throws NoSuchAlgorithmException if the algorithm used to check
     *         the integrity of the keyStore cannot be found.
     */
    protected PublicKey getPublicKey() throws  LicenseNotaryException,
            IOException,
            CertificateException,
            NoSuchAlgorithmException {
        if (publicKey == null) {
            final String alias = getKeyStoreParam().getAlias();
            final KeyStore keystore = getKeyStore();
            try {
                if ((getKeyStoreParam().getKeyPwd() != null)
                        != keystore.isKeyEntry(alias))
                    throw new LicenseNotaryException(
                            EXC_PRIVATE_KEY_OR_PWD_IS_NOT_ALLOWED, alias);
                final Certificate cert = keystore.getCertificate(alias);
                if (cert == null)
                    throw new LicenseNotaryException(
                            EXC_NO_CERTIFICATE_ENTRY, alias);
                publicKey = cert.getPublicKey();
            } catch (KeyStoreException keystoreIsAlreadyLoaded) {
                throw new AssertionError(keystoreIsAlreadyLoaded);
            }
        }

        return publicKey;
    }

    /**
     * Returns a valid signature engine to be used for signing and verifying
     * a {@link GenericCertificate} - {@code null} is never returned.
     */
    protected Signature getSignatureEngine() {
        try {
            return Signature.getInstance(SHA1_WITH_DSA);
        }
        catch (NoSuchAlgorithmException cannotHappen) {
            throw new AssertionError(cannotHappen);
        }
    }

    /**
     * Returns a loaded/initialized keyStore.
     * 
     * @throws IOException if there is an I/O or format problem with the
     *         keyStore data.
     * @throws CertificateException if any of the certificates in the
     *         keyStore could not be loaded.
     * @throws NoSuchAlgorithmException if the algorithm used to check
     *         the integrity of the keyStore cannot be found.
     */
    protected KeyStore getKeyStore() throws  IOException,
            CertificateException,
            NoSuchAlgorithmException {
        if (keyStore != null)
            return keyStore;

        InputStream in = null;
        try {
            keyStore = KeyStore.getInstance(JKS);
            in = new BufferedInputStream(param.getStream(), BUFSIZE);
            keyStore.load(in, getKeyStoreParam().getStorePwd().toCharArray());
        } catch (KeyStoreException cannotHappen) {
            throw new AssertionError(cannotHappen);
        } finally {
            try { in.close(); } // May throw NullPointerException!
            catch (Exception weDontCare) { }
        }
        
        return keyStore;
    }
}
