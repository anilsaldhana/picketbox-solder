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
package org.picketbox.solder.authentication.http;

import java.util.Map;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.picketbox.authentication.AuthenticationManager;
import org.picketbox.authentication.PicketBoxConstants;
import org.picketbox.authentication.http.AbstractHTTPAuthentication;
import org.picketbox.authentication.http.HTTPAuthenticationScheme;
import org.picketbox.authentication.http.HTTPAuthenticationSchemeLoader;

/**
 * A Solder based implementation of {@link HTTPAuthenticationSchemeLoader}
 * @author anil saldhana
 * @since Jul 10, 2012
 */
public class SolderHTTPAuthenticationSchemeLoader implements HTTPAuthenticationSchemeLoader {

    @Inject
    private HTTPAuthenticationScheme httpAuthScheme = null;
    
    /*
     * (non-Javadoc)
     * @see org.picketbox.authentication.http.HTTPAuthenticationSchemeLoader#get(java.util.Map)
     */
    @Override
    public HTTPAuthenticationScheme get(Map<String, Object> contextData) throws ServletException {
        ServletContext servletContext = (ServletContext) contextData.get(PicketBoxConstants.SERVLET_CONTEXT);
        AuthenticationManager authMgr = (AuthenticationManager) contextData.get(PicketBoxConstants.AUTH_MGR);
        
        if(httpAuthScheme == null){
            throw new ServletException("HTTP Authentication Scheme has not been injected");
        } 
        
        if(httpAuthScheme instanceof AbstractHTTPAuthentication){
            AbstractHTTPAuthentication abstractHTTP = (AbstractHTTPAuthentication) httpAuthScheme;
            abstractHTTP.setAuthManager(authMgr);
            abstractHTTP.setServletContext(servletContext);
        }
        return httpAuthScheme;
    }
}