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

package org.picketbox.solder;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.jboss.solder.servlet.event.Initialized;
import org.picketbox.core.PicketBoxConfiguration;
import org.picketbox.core.PicketBoxManager;
import org.picketbox.core.authentication.http.AbstractHTTPAuthentication;
import org.picketbox.core.authentication.http.HTTPAuthenticationScheme;
import org.picketbox.core.authorization.AuthorizationManager;
import org.picketbox.solder.authentication.AuthenticationScheme;

/**
 * <p>
 * This class provides an integration point to Solder. It is a listener for the {@link ServletContext} initialization event that
 * builds and creates a {@link PicketBoxManager} instance.
 * </p>
 * <p>
 * It is mandatory to have a <i>META-INF/seam-beans.xml</i> file where your PicketBox configuration should be defined.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 */
@ApplicationScoped
public class PicketBoxListener {

    @Inject
    @AuthenticationScheme
    private HTTPAuthenticationScheme authenticationScheme;

    /**
     * <p>
     * {@link Instance} instance to get the {@link AuthorizationManager}. Since the authorization configuration is optional, the
     * manager can not be inject as an usual injection point.
     * </p>
     */
    @Inject
    private Instance<AuthorizationManager> authorizationManager;

    /**
     * <p>
     * Stores and produces a {@link PicketBoxManager} instance. The instance can be injected in any CDI bean as an usual
     * injection point.
     * </p>
     */
    @SuppressWarnings("unused")
    @Produces
    @ApplicationScoped
    private PicketBoxManager picketBoxManager;

    /**
     * <p>
     * Observes the {@link ServletContext} initialization and configures the {@link PicketBoxManager}.
     * </p>
     */
    public void initializePicketBoxManager(@Observes @Initialized ServletContext servletContext) {
        if (this.authenticationScheme instanceof AbstractHTTPAuthentication) {
            ((AbstractHTTPAuthentication) this.authenticationScheme).setServletContext(servletContext);
        }

        AuthorizationManager authorizationManager = null;

        try {
            authorizationManager = this.authorizationManager.get();
        } catch (Exception e) {

        }

        this.picketBoxManager = new PicketBoxConfiguration().authentication(this.authenticationScheme)
                .authorization(authorizationManager).buildAndStart();
    }

}