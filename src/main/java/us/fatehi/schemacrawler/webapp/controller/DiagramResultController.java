/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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
package us.fatehi.schemacrawler.webapp.controller;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.newBufferedReader;
import static us.fatehi.schemacrawler.webapp.controller.URIConstants.API_PREFIX;
import static us.fatehi.schemacrawler.webapp.controller.URIConstants.UI_RESULTS_PREFIX;
import static us.fatehi.schemacrawler.webapp.service.storage.FileExtensionType.JSON;
import static us.fatehi.schemacrawler.webapp.service.storage.FileExtensionType.PNG;

import java.nio.file.Path;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import us.fatehi.schemacrawler.webapp.model.DiagramKey;
import us.fatehi.schemacrawler.webapp.model.DiagramRequest;
import us.fatehi.schemacrawler.webapp.service.processing.ProcessingService;
import us.fatehi.schemacrawler.webapp.service.storage.StorageService;

@Controller
public class DiagramResultController {

  private static final Logger LOGGER = LoggerFactory.getLogger(DiagramResultController.class);

  private final StorageService storageService;

  @Autowired
  public DiagramResultController(
      @NotNull final StorageService storageService,
      @NotNull final ProcessingService processingService) {
    this.storageService = storageService;
  }

  @GetMapping(
      value = {API_PREFIX + "/{key}/diagram", UI_RESULTS_PREFIX + "/{key}/diagram"},
      produces = MediaType.IMAGE_PNG_VALUE)
  @ResponseBody
  public Resource diagramImage(
      @PathVariable @NotNull @Pattern(regexp = "[A-Za-z0-9]{12}") @Size(min = 12, max = 12)
          final DiagramKey key)
      throws Exception {
    return retrieveDiagramLocal(key);
  }

  /**
   * Retrieve results as HTML using a rendered Thymeleaf template.
   *
   * @param key Diagram key for the results.
   * @return Diagram request data, including the key
   * @throws Exception On an exception
   */
  @GetMapping(value = UI_RESULTS_PREFIX + "/{key}")
  public String retrieveResults(
      final Model model,
      @PathVariable @NotNull @Pattern(regexp = "[A-Za-z0-9]{12}") @Size(min = 12, max = 12)
          final DiagramKey key)
      throws Exception {

    final DiagramRequest diagramRequest = retrieveResults(key);
    model.addAttribute("diagramRequest", diagramRequest);

    if (diagramRequest.hasLogMessage()) {
      throw new ExecutionRuntimeException(diagramRequest.getError());
    }

    return "SchemaCrawlerDiagram";
  }

  /**
   * Retrieve results as a JSON object.
   *
   * @param key Diagram key for the results.
   * @return Diagram request data, including the key
   * @throws Exception On an exception
   */
  @GetMapping(value = API_PREFIX + "/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<DiagramRequest> retrieveResultsApi(
      @PathVariable @NotNull @Pattern(regexp = "[A-Za-z0-9]{12}") @Size(min = 12, max = 12)
          final DiagramKey key)
      throws Exception {

    final DiagramRequest diagramRequest;
    try {
      diagramRequest = retrieveResults(key);
    } catch (final Exception e) {
      LOGGER.warn(e.getMessage(), e);
      return ResponseEntity.notFound().build();
    }

    if (diagramRequest.hasLogMessage()) {
      return ResponseEntity.badRequest().body(diagramRequest);
    } else {
      return ResponseEntity.ok(diagramRequest);
    }
  }

  private Resource retrieveDiagramLocal(final DiagramKey key) throws Exception {
    return storageService
        .retrieveLocal(key, PNG)
        .map(PathResource::new)
        .orElseThrow(() -> new Exception("Cannot find image, " + key));
  }

  private DiagramRequest retrieveResults(final DiagramKey key) throws Exception {
    final Path jsonFile =
        storageService
            .retrieveLocal(key, JSON)
            .orElseThrow(() -> new ExecutionRuntimeException("Cannot find request for " + key));
    final DiagramRequest diagramRequest =
        DiagramRequest.fromJson(newBufferedReader(jsonFile, UTF_8));
    return diagramRequest;
  }
}
