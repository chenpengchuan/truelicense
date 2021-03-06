/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package de.schlichtherle.license;

import de.schlichtherle.util.ObfuscatedString;
import de.schlichtherle.xml.GenericCertificate;
import de.schlichtherle.xml.PersistenceService;
import de.schlichtherle.xml.PersistenceServiceException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * This class provides encoding and encryption services to provide long time
 * persistence for {@link GenericCertificate}s and protect the privacy of its
 * data.
 * <p>
 * This class is <em>not</em> thread safe.
 *
 * @author Christian Schlichtherle
 * @version $Id$
 */
public class PrivacyGuard {

    private static final String PBE_WITH_MD5_AND_DES = new ObfuscatedString(new long[] {
        0x27B2E8783E47F1ABL, 0x45CF8AD4390DC9D8L, 0xAB320350966BC9BFL
    }).toString(); /* => "PBEWithMD5AndDES" */

    private CipherParam param; // initialized by setCipherParam() - should be accessed via getCipherParam() only!

    //
    // Data computed and cached from the cipher configuration parameters.
    //

    private Cipher cipher;
    private SecretKey key;
    private AlgorithmParameterSpec algoParamSpec;

    /**
     * Creates a new Privacy Guard.
     * <p>
     * <b>Warning:</b> The guard created by this constructor is <em>not</em>
     * valid and cannot be used unless {@link #setCipherParam(CipherParam)}
     * is called!
     */
    protected PrivacyGuard() {
    }

    /**
     * Creates a new Privacy Guard.
     *
     * @param param The cipher configuration parameters
     *        - may <em>not</em> be {@code null}.
     */
    public PrivacyGuard(CipherParam param) {
        setCipherParam0(param);
    }

    /**
     * Returns the cipher configuration parameters.
     */
    public CipherParam getCipherParam() {
        return param;
    }

    /**
     * Sets the cipher configuration parameters.
     * Calling this method resets the guard as if it had been
     * newly created.
     * Some plausibility checks are applied to the given parameter object
     * to ensure that it adheres to the contract of the parameter interfaces.
     *
     * @param param the cipher configuration parameters
     *        - may <em>not</em> be {@code null}.
     * @throws NullPointerException if the given parameter object does not
     *         obey the contract of its interface due to a {@code null}
     *         pointer.
     * @throws IllegalPasswordException if any password in the parameter object
     *         does not comply to the current policy.
     */
    public void setCipherParam(CipherParam param) {
        setCipherParam0(param);
    }

    private void setCipherParam0(CipherParam param) {
        // Check parameters to implement fail-fast behaviour.
        if (param == null)
            throw new NullPointerException(LicenseNotary.PARAM);
        Policy.getCurrent().checkPwd(param.getKeyPwd());

        this.param = param;
        cipher = null;
        key = null;
        algoParamSpec = null;
    }

    /**
     * Encodes, compresses and encrypts the given license certificate
     * and returns the result as a license key.
     * Please note that this method does not sign the certificate.
     *
     * @param certificate The license certificate
     *        - may <em>not</em> be {@code null}.
     *
     * @return The license key
     *         - {@code null} is never returned.
     *
     * @throws Exception An instance of a subclass of this class for various
     *         reasons.
     *         Note that you should always use
     *         {@link Throwable#getLocalizedMessage()} to get a (possibly
     *         localized) meaningful detail message.
     */
    public byte[] cert2key(final GenericCertificate certificate)
    throws Exception {
        // Encode the certificate and store it to a file.
        final ByteArrayOutputStream keyOut = new ByteArrayOutputStream();
        final OutputStream out = new GZIPOutputStream(
                new CipherOutputStream(
                    keyOut,
                    getCipher4Encryption()));
        try {
            PersistenceService.store(certificate, out);
        }
        catch (PersistenceServiceException cannotHappen) {
            throw new AssertionError(cannotHappen);
        }
        return keyOut.toByteArray();
    }

    /**
     * Decrypts, decompresses and decodes the given license key
     * and returns the result as a license certificate.
     * Please note that this method does not verify the certificate.
     *
     * @param key The license key to process
     *        - may <em>not</em> be {@code null}.
     *
     * @return The license certificate
     *         - {@code null} is never returned.
     *
     * @throws Exception An instance of a subclass of this class for various
     *         reasons.
     *         Note that you should always use
     *         {@link Throwable#getLocalizedMessage()} to get a (possibly
     *         localized) meaningful detail message.
     */
    public GenericCertificate key2cert(final byte[] key)
    throws Exception {
        final InputStream in = new GZIPInputStream(
                new ByteArrayInputStream(
                    getCipher4Decryption().doFinal(key)));
        final GenericCertificate certificate;
        try {
            certificate = (GenericCertificate) PersistenceService.load(in);
        }
        finally {
            try { in.close(); }
            catch (IOException weDontCare) { }
        }

        return certificate;
    }

    /**
     * Returns a cipher object which is initialised for encryption
     * - {@code null} is never returned.
     *
     * @deprecated <b>Experimental:</b> Methods marked with this note have
     *             been tested to be functional but may change or disappear
     *             at will in one of the next releases because they are still
     *             a topic for research on extended functionality.
     *             Most likely the methods will prevail however and this note
     *             will just vanish, so you may use them with a certain risk.
     */
    protected Cipher getCipher4Encryption() {
        Cipher cipher = getCipher();
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key, algoParamSpec);
        }
        catch (InvalidKeyException cannotHappen) {
            throw new AssertionError(cannotHappen);
        }
        catch (InvalidAlgorithmParameterException cannotHappen) {
            throw new AssertionError(cannotHappen);
        }
        
        return cipher;
    }

    /**
     * Returns a cipher object which is initialised for decryption
     * - {@code null} is never returned.
     *
     * @deprecated <b>Experimental:</b> Methods marked with this note have
     *             been tested to be functional but may change or disappear
     *             at will in one of the next releases because they are still
     *             a topic for research on extended functionality.
     *             Most likely the methods will prevail however and this note
     *             will just vanish, so you may use them with a certain risk.
     */
    protected Cipher getCipher4Decryption() {
        Cipher cipher = getCipher();
        try {
            cipher.init(Cipher.DECRYPT_MODE, key, algoParamSpec);
        }
        catch (InvalidKeyException cannotHappen) {
            throw new AssertionError(cannotHappen);
        }
        catch (InvalidAlgorithmParameterException cannotHappen) {
            throw new AssertionError(cannotHappen);
        }
        
        return cipher;
    }

    /**
     * Returns a cipher object which needs to be configured for encryption or
     * decryption
     * - {@code null} is never returned.
     *
     * @deprecated <b>Experimental:</b> Methods marked with this note have
     *             been tested to be functional but may change or disappear
     *             at will in one of the next releases because they are still
     *             a topic for research on extended functionality.
     *             Most likely the methods will prevail however and this note
     *             will just vanish, so you may use them with a certain risk.
     */
    protected Cipher getCipher() {
        if (cipher != null)
            return cipher;
        algoParamSpec = new PBEParameterSpec(
            new byte[] {
                (byte)0xce, (byte)0xfb, (byte)0xde, (byte)0xac,
                (byte)0x05, (byte)0x02, (byte)0x19, (byte)0x71
            },
            2005);
        try {
            KeySpec keySpec = new PBEKeySpec(getCipherParam().getKeyPwd().toCharArray());
            SecretKeyFactory keyFac = SecretKeyFactory.getInstance(PBE_WITH_MD5_AND_DES);
            key = keyFac.generateSecret(keySpec);
            cipher = Cipher.getInstance(PBE_WITH_MD5_AND_DES);
        } catch (NoSuchAlgorithmException cannotHappen) {
            throw new AssertionError(cannotHappen);
        } catch (InvalidKeySpecException cannotHappen) {
            throw new AssertionError(cannotHappen);
        } catch (NoSuchPaddingException cannotHappen) {
            throw new AssertionError(cannotHappen);
        }
        return cipher;
    }
}
