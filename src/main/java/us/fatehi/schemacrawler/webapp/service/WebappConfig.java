/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package us.fatehi.schemacrawler.webapp.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;

@Configuration
public class WebappConfig {

  @Value("${SC_WEB_APP_URI:none}")
  private String webAppUri;

  @Bean(name = "webAppUri")
  public String webAppUri() {
    if (StringUtils.isBlank(webAppUri)) {
      throw new InternalRuntimeException("No web application URI provided");
    }
    if (webAppUri.equals("none")) {
      return "http://localhost:8080";
    }
    return webAppUri;
  }
}
