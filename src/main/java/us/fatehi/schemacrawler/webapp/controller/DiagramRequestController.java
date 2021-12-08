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
package us.fatehi.schemacrawler.webapp.controller;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.IOUtils.toInputStream;
import static us.fatehi.schemacrawler.webapp.service.storage.FileExtensionType.DATA;
import static us.fatehi.schemacrawler.webapp.service.storage.FileExtensionType.JSON;
import static us.fatehi.schemacrawler.webapp.service.storage.FileExtensionType.LOG;
import static us.fatehi.schemacrawler.webapp.service.storage.FileExtensionType.SQLITE_DB;
import static us.fatehi.utility.Utility.isBlank;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.tika.Tika;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import us.fatehi.schemacrawler.webapp.model.DiagramKey;
import us.fatehi.schemacrawler.webapp.model.DiagramRequest;
import us.fatehi.schemacrawler.webapp.service.processing.ProcessingService;
import us.fatehi.schemacrawler.webapp.service.storage.StorageService;

@Controller
public class DiagramRequestController {

  private static final Logger LOGGER = LoggerFactory.getLogger(DiagramRequestController.class);

  private final StorageService storageService;
  private final ProcessingService processingService;

  @Autowired
  public DiagramRequestController(
      @NotNull final StorageService storageService,
      @NotNull final ProcessingService processingService) {
    this.storageService = storageService;
    this.processingService = processingService;
  }

  @GetMapping("/schemacrawler")
  public String diagramRequestForm(@NotNull final Model model) {
    model.addAttribute("diagramRequest", new DiagramRequest());
    return "SchemaCrawlerDiagramForm";
  }

  @PostMapping(value = "/schemacrawler", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<DiagramRequest> diagramRequestFormApi(
      @ModelAttribute("diagramRequest") @NotNull @Valid final DiagramRequest diagramRequest,
      final BindingResult bindingResult,
      @RequestParam("file") final Optional<MultipartFile> file) {

    if (!file.isPresent()) {
      diagramRequest.setLogMessage("No SQLite file upload provided");
    } else if (bindingResult.hasErrors() || !file.isPresent()) {
      final List<String> errors =
          bindingResult.getFieldErrors().stream()
              .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
              .collect(Collectors.toList());
      Collections.sort(errors);
      diagramRequest.setLogMessage(errors.toString());
    } else {
      try {
        generateSchemaCrawlerDiagram(diagramRequest, file.get());
      } catch (final Exception e) {
        LOGGER.warn(e.getMessage(), e);
        diagramRequest.setLogMessage(e.getMessage());
      }
    }

    if (diagramRequest.hasLogMessage()) {
      return ResponseEntity.badRequest().body(diagramRequest);
    } else {
      return ResponseEntity.ok(diagramRequest);
    }
  }

  // http://stackoverflow.com/questions/30297719/cannot-get-validation-working-with-spring-boot-and-thymeleaf
  @PostMapping(value = "/schemacrawler")
  public String diagramRequestFormSubmit(
      @ModelAttribute("diagramRequest") @NotNull @Valid final DiagramRequest diagramRequest,
      final BindingResult bindingResult,
      @RequestParam("file") final MultipartFile file)
      throws Exception {
    if (bindingResult.hasErrors()) {
      return "SchemaCrawlerDiagramForm";
    }

    generateSchemaCrawlerDiagram(diagramRequest, file);

    return "SchemaCrawlerDiagramResult";
  }

  @GetMapping(value = "/")
  public String index() {
    return "redirect:/schemacrawler";
  }

  private void checkMimeType(final DiagramRequest diagramRequest, final Path localPath)
      throws ExecutionRuntimeException {
    try {
      final String detectedMimeType = new Tika().detect(localPath);
      if (!detectedMimeType.equals("application/x-sqlite3")) {
        final MimeType mimeType = MimeTypes.getDefaultMimeTypes().forName(detectedMimeType);

        final StringBuffer exceptionMessage = new StringBuffer();
        exceptionMessage.append("Expected a SQLite database file, but got a ");
        if (!isBlank(mimeType.getDescription())) {
          exceptionMessage.append(mimeType.getDescription()).append(" file");
        } else if (isBlank(mimeType.getDescription()) && !isBlank(detectedMimeType)) {
          exceptionMessage.append("file of type ").append(detectedMimeType);
        } else {
          exceptionMessage.append("file of an unknown type");
        }
        throw new ExecutionRuntimeException(exceptionMessage.toString());
      }
    } catch (final MimeTypeException | IOException | NullPointerException e) {
      LOGGER.trace(e.getMessage(), e);
    }
  }

  private void generateSchemaCrawlerDiagram(
      final DiagramRequest diagramRequest, final MultipartFile file) throws Exception {

    final DiagramKey key = diagramRequest.getKey();
    try {

      // Store the uploaded database file locally, so it can be processed
      final Path localPath = storageService.storeLocal(file, key, SQLITE_DB);

      checkMimeType(diagramRequest, localPath);

      // Make asynchronous call to generate diagram
      processingService.generateSchemaCrawlerDiagram(diagramRequest, localPath);
    } catch (final Exception e) {
      LOGGER.warn(e.getMessage(), e);
      saveExceptionLogFile(key, e);
      diagramRequest.setLogMessage(e.getMessage());
      // Save a copy of the uploaded file, which may not be a SQLite database
      storageService.store(file, key, DATA);
      throw e;
    } finally {
      // Store the JSON request
      storageService.store(() -> toInputStream(diagramRequest.toJson(), UTF_8), key, JSON);
    }
  }

  private void saveExceptionLogFile(final DiagramKey key, final Exception e) {
    try { // Write out stack trace to a log file, and save it
      final StringWriter stackTraceWriter = new StringWriter();
      e.printStackTrace(new PrintWriter(stackTraceWriter));
      final String stackTrace = stackTraceWriter.toString();
      storageService.store(() -> toInputStream(stackTrace, UTF_8), key, LOG);
    } catch (final Exception ex) {
      LOGGER.warn(e.getMessage(), e);
    }
  }
}
