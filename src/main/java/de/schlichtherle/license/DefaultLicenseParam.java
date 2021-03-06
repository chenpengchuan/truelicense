/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package de.schlichtherle.license;

import java.util.prefs.Preferences;

/**
 * This is a convenience class implementing the {@link LicenseParam} interface.
 * 
 * @author Christian Schlichtherle
 * @version $Id$
 */
public class DefaultLicenseParam implements LicenseParam {
    
    private final String subject;
    private final Preferences preferences;
    private final KeyStoreParam keyStoreParam;
    private final CipherParam cipherParam;
    
    /**
     * Creates a new instance of DefaultLicenseParam.
     * 
     * @param subject The licensing subject
     *        to be returned by {@link #getSubject()}.
     * @param preferences The preferences node used to store the license key
     *        to be returned by {@link #getPreferences()}.
     * @param keyStoreParam The key store parameters
     *        to be returned by {@link #getKeyStoreParam()}.
     * @param cipherParam The cipher parameters
     *        to be returned by {@link #getCipherParam()}.
     */
    public DefaultLicenseParam(
            String subject,
            Preferences preferences,
            KeyStoreParam keyStoreParam,
            CipherParam cipherParam) {
        this.subject = subject;
        this.preferences = preferences;
        this.keyStoreParam = keyStoreParam;
        this.cipherParam = cipherParam;
    }

    public String getSubject() {
        return subject;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public KeyStoreParam getKeyStoreParam() {
        return keyStoreParam;
    }

    public CipherParam getCipherParam() {
        return cipherParam;
    }
}
