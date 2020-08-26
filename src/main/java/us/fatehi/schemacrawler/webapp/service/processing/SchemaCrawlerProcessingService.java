/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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
package us.fatehi.schemacrawler.webapp.service.processing;


import static us.fatehi.schemacrawler.webapp.service.storage.FileExtensionType.PNG;
import static us.fatehi.schemacrawler.webapp.service.storage.FileExtensionType.SQLITE_DB;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.nio.file.Path;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.PathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import us.fatehi.schemacrawler.webapp.service.schemacrawler.SchemaCrawlerService;
import us.fatehi.schemacrawler.webapp.service.storage.StorageService;

@Service
public class SchemaCrawlerProcessingService
  implements ProcessingService
{

  private final static Logger logger =
    Logger.getLogger(SchemaCrawlerProcessingService.class.getName());

  private final StorageService storageService;
  private final SchemaCrawlerService scService;

  @Autowired
  public SchemaCrawlerProcessingService(
    @NotNull final StorageService storageService,
    @NotNull final SchemaCrawlerService scService)
  {
    this.storageService = storageService;
    this.scService = scService;
  }

  /**
   * {@inheritDoc}
   */
  // @Async
  @Override
  public void generateSchemaCrawlerDiagram(
    @NotNull @Pattern(regexp = "[A-Za-z0-9]{12}") @Size(min = 12, max = 12)
    final String key)
    throws Exception
  {
    logger.info("Executing in thread, " + Thread
      .currentThread()
      .getName());

    // Generate a database integration, and store the generated image
    final Path dbFile = storageService
      .resolve(key, SQLITE_DB)
      .orElseThrow(() -> new Exception(String.format(
        "Cannot locate database file, %s",
        key)));
    final Path schemaCrawlerDiagram =
      scService.createSchemaCrawlerDiagram(dbFile, PNG.getExtension());
    storageService.store(new PathResource(schemaCrawlerDiagram), key, PNG);
  }

}