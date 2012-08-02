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

package org.picketbox.solder.authentication;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.solder.servlet.ServletRequestContext;
import org.jboss.solder.servlet.event.Initialized;
import org.picketbox.core.PicketBoxMessages;
import org.picketbox.core.exceptions.AuthenticationException;
import org.picketbox.http.PicketBoxHTTPManager;
import org.picketbox.http.authentication.HTTPAuthenticationScheme;
import org.picketbox.http.authorization.resource.WebResource;

/**
 * <p>
 * This class is an integration point with Solder to provide authentication capabilities. Each request is intercepted by this
 * component to execute authentication operations.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
@ApplicationScoped
public class AuthenticationManager {

    @Inject
    private PicketBoxHTTPManager securityManager;

    @Inject
    @AuthenticationScheme
    private HTTPAuthenticationScheme authenticationScheme;

    /**
     * <p>
     * Observes the {@link HttpServletRequest} and executes authentication.
     * </p>
     */
    @SuppressWarnings("unchecked")
    public void observeRequest(@Observes @Initialized ServletRequestContext requestContext) throws AuthenticationException {
        try {
            this.authenticationScheme.setPicketBoxManager(this.securityManager);

            HttpServletRequest request = (HttpServletRequest) requestContext.getRequest();
            HttpServletResponse response = (HttpServletResponse) requestContext.getResponse();

            if (isUserNotAuthenticated(request)
                    && this.securityManager.getProtectedResource(createWebResource(request, response))
                            .requiresAuthentication()) {
                authenticate(request, response);
            }
        } catch (AuthenticationException e) {
            throw PicketBoxMessages.MESSAGES.authenticationFailed(e);
        }
    }

    private WebResource createWebResource(HttpServletRequest request, HttpServletResponse response) {
        WebResource resource = new WebResource();

        resource.setContext(request.getServletContext());
        resource.setRequest(request);
        resource.setResponse(response);

        return resource;
    }

    /**
     * <p>
     * Checks if the user is already authenticated.
     * </p>
     */
    private boolean isUserNotAuthenticated(HttpServletRequest request) {
        return !this.securityManager.isAuthenticated(request);
    }

    /**
     * <p>
     * Initiates the authentication process.
     * </p>
     */
    private void authenticate(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!response.isCommitted()) {
            this.authenticationScheme.authenticate(request, response);
        }
    }

}