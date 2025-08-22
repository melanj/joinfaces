/*
 * Copyright 2016-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.joinfaces.servlet;

import java.util.Collection;
import java.util.EventListener;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Singular;

import org.springframework.boot.jetty.servlet.JettyServletWebServerFactory;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.server.servlet.ConfigurableServletWebServerFactory;
import org.springframework.boot.web.server.servlet.Jsp;
import org.springframework.util.ClassUtils;

/**
 * This class registers listeners to the servlet context, which would normally registered in a tld file.
 * <p>
 * Some JSF libraries declare listeners in JSP taglib files. Jetty (since 9.4.14.v20181114)
 * doesn't find there listeners, so this class can be used as spring bean to manually add the listeners to an
 * embedded jetty.
 * <p>
 * Also the embedded Tomcat does not find the listeners, if tomcat-embed-jasper is not deployed.
 *
 * @author Lars Grefer
 * @see ServletContextListenerUtil
 */
@Builder
@RequiredArgsConstructor
public class TldListenerRegistrationBean implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

	@Singular
	private final Collection<Class<? extends EventListener>> listeners;

	@Override
	public void customize(ConfigurableServletWebServerFactory factory) {
		if (mustAddInitializer(factory)) {
			ServletContextListenerUtil.addListeners(factory, listeners);
		}
	}

	private boolean mustAddInitializer(ConfigurableServletWebServerFactory factory) {
		if (SpringBootServerUtil.isSpringBootJettyAvailable() && factory instanceof JettyServletWebServerFactory) {
			return true;
		}

		if (SpringBootServerUtil.isSpringBootTomcatAvailable() && factory instanceof TomcatServletWebServerFactory) {
			Jsp jsp = factory.getSettings().getJsp();
			return !jsp.getRegistered() || !ClassUtils.isPresent(jsp.getClassName(), null);
		}

		return false;
	}
}
