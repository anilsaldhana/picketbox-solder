/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.picketbox.solder.test.authentication.http;

import java.security.Principal;
import java.security.cert.X509Certificate;

import org.picketbox.authentication.AuthenticationManager;
import org.picketbox.authentication.DigestHolder;
import org.picketbox.authentication.http.HTTPDigestAuthentication;
import org.picketbox.exceptions.AuthenticationException;
import org.picketbox.exceptions.FormatException;
import org.picketbox.util.HTTPDigestUtil;

/**
 * <p>{@link AuthenticationManager} class to be used during the tests using a {@link HTTPDigestAuthentication}.</p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 */
public class MockHTTPDigestAuthenticationManager implements AuthenticationManager {

    /* (non-Javadoc)
     * @see org.picketbox.authentication.AuthenticationManager#authenticate(java.lang.String, java.lang.Object)
     */
    @Override
    public Principal authenticate(final String username, Object credential) throws AuthenticationException {
        throw new UnsupportedOperationException("Not implemented.");
    }

    /* (non-Javadoc)
     * @see org.picketbox.authentication.AuthenticationManager#authenticate(org.picketbox.authentication.DigestHolder)
     */
    @Override
    public Principal authenticate(final DigestHolder digest) throws AuthenticationException {
        String storedPassword = "Circle Of Life";

        try {
            if (HTTPDigestUtil.matchCredential(digest, storedPassword.toCharArray())) {
                return new Principal() {
                    @Override
                    public String getName() {
                        return digest.getUsername();
                    }
                };
            }
        } catch (FormatException e1) {
            throw new AuthenticationException(e1);
        }

        return null;
    }

    /* (non-Javadoc)
     * @see org.picketbox.authentication.AuthenticationManager#authenticate(java.security.cert.X509Certificate[])
     */
    @Override
    public Principal authenticate(X509Certificate[] certs) throws AuthenticationException {
        throw new UnsupportedOperationException("Not implemented.");
    }

}
