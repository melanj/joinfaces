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

package org.joinfaces.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingFilterBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.servlet.filter.OrderedRequestContextFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.RequestContextFilter;

/**
 * Joinfaces Auto Configuration.
 *
 * @author Lars Grefer
 */
@AutoConfiguration(afterName = "org.springframework.boot.webmvc.autoconfigure.WebMvcAutoConfiguration")
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(OrderedRequestContextFilter.class)
@ImportRuntimeHints(JoinfacesRuntimeHintsRegistrar.class)
public class JoinfacesAutoConfiguration {

	/**
	 * This registers a {@link RequestContextFilter} in case {@link org.springframework.boot.webmvc.autoconfigure.WebMvcAutoConfiguration} is not loaded.
	 *
	 * @return The {@link RequestContextFilter} Bean.
	 * @see WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter#requestContextFilter()
	 */
	@Bean
	@ConditionalOnMissingBean({RequestContextListener.class, RequestContextFilter.class})
	@ConditionalOnMissingFilterBean(RequestContextFilter.class)
	public static RequestContextFilter requestContextFilter() {
		return new OrderedRequestContextFilter();
	}
}
