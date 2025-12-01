/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
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
package us.fatehi.schemacrawler.webapp.test.service.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import us.fatehi.schemacrawler.webapp.model.DiagramRequest;
import us.fatehi.schemacrawler.webapp.service.notification.NotificationService;

@Service("logNotificationService")
@Profile("local")
public class LogNotificationService implements NotificationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(LogNotificationService.class);

  @Override
  public void notify(final DiagramRequest diagramRequest) {
    LOGGER.info("**** EMAILING RESULT for %s".formatted(diagramRequest.getKey().getKey()));
  }
}
