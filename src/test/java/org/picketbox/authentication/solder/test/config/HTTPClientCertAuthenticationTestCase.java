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
package org.picketbox.authentication.solder.test.config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.picketbox.authentication.PicketBoxConstants;
import org.picketbox.authentication.http.HTTPClientCertAuthentication;
import org.picketbox.test.http.TestServletContext;
import org.picketbox.test.http.TestServletRequest;
import org.picketbox.test.http.TestServletResponse;

/**
/**
 * <p>Unit test the {@link HTTPClientCertAuthentication} class.</p>
 * @author anil saldhana 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * 
 * @since July 9, 2012
 */
public class HTTPClientCertAuthenticationTestCase extends AbstractHTTPAuthenticationTestCase {

    @Inject
    private HTTPClientCertAuthentication httpClientCert = null;

    private TestServletContext sc = new TestServletContext(new HashMap<String, String>());
    
    @Before
    public void setup() throws Exception {
        httpClientCert.setServletContext(sc);
    }

    @Test
    public void testHttpClientCert() throws Exception {
        TestServletRequest req = new TestServletRequest(new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        });

        TestServletResponse resp = new TestServletResponse(new OutputStream() {

            @Override
            public void write(int b) throws IOException {
                System.out.println(b);
            }
        });

        InputStream bis = getClass().getClassLoader().getResourceAsStream("cert/servercert.txt");

        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) cf.generateCertificate(bis);
        bis.close();

        assertNotNull(cert);

        // Call the server to get the digest challenge
        boolean result = httpClientCert.authenticate(req, resp);
        assertFalse(result);

        // Now set the certificate
        req.setAttribute(PicketBoxConstants.HTTP_CERTIFICATE, new X509Certificate[] { cert });

        result = httpClientCert.authenticate(req, resp);
        assertTrue(result);
    }
}