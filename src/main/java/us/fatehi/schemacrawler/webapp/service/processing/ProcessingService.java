/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.IOUtils.toInputStream;
import static us.fatehi.schemacrawler.webapp.service.storage.FileExtensionType.JSON;
import static us.fatehi.schemacrawler.webapp.service.storage.FileExtensionType.PNG;
import static us.fatehi.schemacrawler.webapp.service.storage.FileExtensionType.SQLITE_DB;

import java.nio.file.Path;
import java.util.logging.Logger;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.PathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import us.fatehi.schemacrawler.webapp.model.DiagramKey;
import us.fatehi.schemacrawler.webapp.model.DiagramRequest;
import us.fatehi.schemacrawler.webapp.service.schemacrawler.DiagramService;
import us.fatehi.schemacrawler.webapp.service.storage.StorageService;

@Service
public class ProcessingService {

  private static final Logger logger = Logger.getLogger(ProcessingService.class.getName());

  private final StorageService storageService;
  private final DiagramService scService;

  @Autowired
  public ProcessingService(
      @NotNull final StorageService storageService,
      @NotNull final DiagramService scService) {
    this.storageService = storageService;
    this.scService = scService;
  }

  @Async
  public void generateSchemaCrawlerDiagram(
      @NotNull final DiagramRequest diagramRequest, @NotNull final Path localPath)
      throws Exception {

    logger.info(
        String.format(
            "Processing in thread %s%n%s", Thread.currentThread().getName(), diagramRequest));

    final DiagramKey key = diagramRequest.getKey();

    try {
      // Store the uploaded database file
      storageService.store(new PathResource(localPath), key, SQLITE_DB);

      final String title = diagramRequest.getTitle();
      // Generate a database integration, and store the generated image
      final Path schemaCrawlerDiagram =
          scService.createSchemaCrawlerDiagram(localPath, title, PNG.getExtension());
      storageService.store(new PathResource(schemaCrawlerDiagram), key, PNG);

    } catch (final Exception e) {
      diagramRequest.setException(e);
      throw e;
    } finally {
      // Store the JSON request
      storageService.store(
          new InputStreamResource(toInputStream(diagramRequest.toJson(), UTF_8)), key, JSON);
    }
  }
}
