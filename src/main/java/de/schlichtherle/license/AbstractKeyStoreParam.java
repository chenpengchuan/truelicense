/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package de.schlichtherle.license;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * This is a convenience class implementing the
 * {@link KeyStoreParam#getStream()} method.
 *
 * @author  Christian Schlichtherle
 * @version $Id$
 */
public abstract class AbstractKeyStoreParam implements KeyStoreParam {

    private final Class clazz;
    private final String resource;

    /**
     * Creates a new instance of AbstractKeyStoreParam which will look up
     * the given resource using the classloader of the given class when
     * calling {@link #getStream()}.
     */
    protected AbstractKeyStoreParam(final Class clazz, final String resource) {
        if (null == clazz || null == resource)
            throw new NullPointerException();
        this.clazz = clazz;
        this.resource = resource;
    }

    /**
     * Looks up the resource provided to the constructor using the classloader
     * provided to the constructor and returns it as an {@link InputStream}.
     */
    public InputStream getStream() throws IOException {
        InputStream in = clazz.getResourceAsStream(resource);
        if (in == null)
            throw new FileNotFoundException(resource);
        return in;
    }

    /**
     * Returns {@code true} if and only if these key store parameters seem to
     * address the same key store entry as the given object.
     * 
     * @deprecated Not required.
     */
    public final boolean equals(Object object) {
        if (!(object instanceof AbstractKeyStoreParam))
            return false;
        final AbstractKeyStoreParam that = (AbstractKeyStoreParam) object;
        return this.clazz.equals(that.clazz)
                && this.resource.equals(that.resource)
                && this.getAlias().equals(that.getAlias());
    }

    /**
     * Returns a hash code which is consistent with {@link #equals(Object)}.
     * 
     * @return A hash code which is consistent with {@link #equals(Object)}.
     * @deprecated Not required.
     */
    public final int hashCode() {
        int c = 17;
        c = 37 * c + hash(this.clazz);
        c = 37 * c + hash(this.resource);
        return c;
    }

    private static int hash(Object object) {
        return null == object ? 0 : object.hashCode();
    }
}
