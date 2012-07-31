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
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.picketbox.core.PicketBoxManager;
import org.picketbox.core.PicketBoxSubject;

/**
 * <p>
 * This class provides some producer methods to outject PicketBox beans for use by applications.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 */
@ApplicationScoped
public class PicketBoxBeanFactory {

    @Inject
    private PicketBoxManager securityManager;

    /**
     * <p>
     * Produces a {@link PicketBoxSubject} instance with informations about the authenticated user.
     * </p>
     *
     * @param servletReq
     * @return
     */
    @Produces
    @Named("authenticatedUser")
    @RequestScoped
    public PicketBoxSubject produceSubject(HttpServletRequest servletReq) {
        return this.securityManager.getAuthenticatedUser(servletReq);
    }
}