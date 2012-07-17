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
 * This class is an integration point with Solder to provide authentication capabilities. Basically, it provides sobre methods
 * to observe servlet events like context initialization, request context initialization, etc.
 * </p>
 * <p>
 * Each request is intercepted by this component to execute authentication operations.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
@ApplicationScoped
public class PicketBoxListener {

    @Inject
    @AuthenticationScheme
    private HTTPAuthenticationScheme authenticationScheme;

    @Inject
    private Instance<AuthorizationManager> authorizationManager;

    @SuppressWarnings("unused")
    @Produces
    private PicketBoxManager securityManager;

    /**
     * <p>
     * Observes the {@link ServletContext} and initialize the authentication components.
     * </p>
     */
    public void observeServletContextInitialize(@Observes @Initialized ServletContext servletContext) {
        if (this.authenticationScheme instanceof AbstractHTTPAuthentication) {
            ((AbstractHTTPAuthentication) this.authenticationScheme).setServletContext(servletContext);
        }

        AuthorizationManager authorizationManager = null;

        try {
            authorizationManager = this.authorizationManager.get();
        } catch (Exception e) {

        }

        this.securityManager = new PicketBoxConfiguration().authentication(this.authenticationScheme)
                .authorization(authorizationManager).buildAndStart();
    }

}