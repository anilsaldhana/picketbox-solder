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

package org.picketbox.authentication.solder.test;


import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;
import javax.servlet.ServletRequestEvent;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.solder.servlet.event.ServletEventBridgeFilter;
import org.jboss.solder.servlet.event.ServletEventBridgeListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.picketbox.authentication.PicketBoxConstants;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
@RunWith(Arquillian.class)
public class AuthenticationListenerTestCase {
    
    @Inject
    private ServletEventBridgeListener servletEventListener;

    @Inject
    private ServletEventBridgeFilter filter;
    
    @Deployment
    public static Archive<?> createTestArchive() {
        return TestUtil.createBasicTestArchive("seam-beans-auth-test.xml");
    }

    @Test
    public void testHTTPFormAuthentication() throws Exception {
        ServletContext ctx = mock(ServletContext.class);
        HttpSession session = mock(HttpSession.class);
        FilterChain chain = mock(FilterChain.class);
        
        servletEventListener.contextInitialized(new ServletContextEvent(ctx));
        
        performPreAuthentication(ctx, session, chain);
        performAuthentication(ctx, session, chain);
    }

    protected void performAuthentication(ServletContext ctx, HttpSession session, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest authenticationRequest = mock(HttpServletRequest.class);
        HttpServletResponse authenticationResponse = mock(HttpServletResponse.class);
        
        when(authenticationRequest.getServletContext()).thenReturn(ctx);
        when(authenticationRequest.getSession()).thenReturn(session);
        when(authenticationRequest.getSession().getId()).thenReturn("JIAS912323123123123");
        when(authenticationRequest.getSession(true)).thenReturn(session);
        when(authenticationRequest.getSession(false)).thenReturn(session);
        
        when(authenticationRequest.getRequestURI()).thenReturn(PicketBoxConstants.HTTP_FORM_J_SECURITY_CHECK);
        
        when(authenticationRequest.getParameter(PicketBoxConstants.HTTP_FORM_J_USERNAME)).thenReturn("admin");
        when(authenticationRequest.getParameter(PicketBoxConstants.HTTP_FORM_J_PASSWORD)).thenReturn("admin");
        
        final Map<String, Object> sessionAttributes = new HashMap<String, Object>();
        
        Mockito.doAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                sessionAttributes.put(invocation.getArguments()[0].toString(), invocation.getArguments()[1]);
                return null;
            }
        }).when(session).setAttribute(anyString(), anyObject());
        
        servletEventListener.requestInitialized(new ServletRequestEvent(ctx, authenticationRequest));
        filter.doFilter(authenticationRequest, authenticationResponse, chain);
        
        Assert.assertNotNull(sessionAttributes.get(PicketBoxConstants.PRINCIPAL));
    }

    protected void performPreAuthentication(ServletContext ctx, HttpSession session, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest firstRequest = mock(HttpServletRequest.class);
        HttpServletResponse firstResponse = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(firstRequest.getServletContext()).thenReturn(ctx);
        when(firstRequest.getSession()).thenReturn(session);
        when(firstRequest.getSession().getId()).thenReturn("JIAS912323123123123");
        when(firstRequest.getSession(true)).thenReturn(session);
        when(firstRequest.getSession(false)).thenReturn(session);
        when(firstRequest.getHeaderNames()).thenReturn(Collections.enumeration(new ArrayList<String>()));
        when(firstRequest.getCookies()).thenReturn(new ArrayList<Cookie>().toArray(new Cookie[] {}));
        when(firstRequest.getParameterMap()).thenReturn(new HashMap<String, String[]>());
        when(ctx.getRequestDispatcher("/login.jsp")).thenReturn(dispatcher);
        
        when(firstRequest.getRequestURI()).thenReturn("/test/index.jsp");
        
        servletEventListener.requestInitialized(new ServletRequestEvent(ctx, firstRequest));
        filter.doFilter(firstRequest, firstResponse, chain);
    }
    
}
