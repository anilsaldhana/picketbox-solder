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
package org.picketbox.solder.test.config;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.runner.RunWith;
import org.picketbox.core.config.PicketBoxManagerConfiguration;
import org.picketbox.http.PicketBoxHTTPManager;
import org.picketbox.solder.test.TestUtil;

/**
 * <p>
 * Abstract class for test cases using Arquillian.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
@RunWith(Arquillian.class)
public abstract class AbstractHTTPAuthenticationTestCase {

    protected PicketBoxManagerConfiguration configuration = null;

    /**
     * <p>
     * Creates a simple JAR file for testing. The resulting JAR already have the main PicketBox and Solder dependencies.
     * </p>
     */
    @Deployment
    public static Archive<?> createTestArchive() {
        return TestUtil.createBasicTestArchive("seam-beans.xml");
    }

    public void initialize() {
        configuration = new PicketBoxManagerConfiguration();
        configuration.manager(new PicketBoxHTTPManager());
    }
}