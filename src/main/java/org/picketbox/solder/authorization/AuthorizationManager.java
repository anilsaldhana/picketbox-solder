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

package org.picketbox.solder.authorization;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.solder.servlet.ServletRequestContext;
import org.jboss.solder.servlet.event.Initialized;
import org.picketbox.core.PicketBoxManager;
import org.picketbox.core.PicketBoxMessages;
import org.picketbox.core.exceptions.AuthorizationException;

/**
 * <p>
 * This class is an integration point with Solder to provide authorization capabilities.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 */
@ApplicationScoped
public class AuthorizationManager {

    @Inject
    private PicketBoxManager securityManager;

    /**
     * <p>
     * Observes the {@link HttpServletRequest} and executes authorization.
     * </p>
     * @throws AuthorizationException if some problem occurs during the authorization process.
     */
    public void observeRequest(@Observes @Initialized ServletRequestContext requestContext) throws AuthorizationException {
        try {
            HttpServletRequest request = (HttpServletRequest) requestContext.getRequest();
            HttpServletResponse response = (HttpServletResponse) requestContext.getResponse();

            authorize(request, response);
        } catch (IOException e) {
            throw PicketBoxMessages.MESSAGES.authorizationFailed(e);
        }
    }

    /**
     * <p>
     * Perform authorization.
     * </p>
     *
     * @throws IOException if some problem occur redirecting the user to the error page.
     */
    private void authorize(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!this.securityManager.authorize(request, response)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }

}