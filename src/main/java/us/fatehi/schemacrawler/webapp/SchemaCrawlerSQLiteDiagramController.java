/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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
package us.fatehi.schemacrawler.webapp;


import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import us.fatehi.schemacrawler.webapp.model.SchemaCrawlerDiagramRequest;
import us.fatehi.schemacrawler.webapp.schemacrawler.SchemaCrawlerService;
import us.fatehi.schemacrawler.webapp.storage.StorageService;

@Controller
public class SchemaCrawlerSQLiteDiagramController
{

  private static Logger logger = LoggerFactory
    .getLogger(SchemaCrawlerSQLiteDiagramController.class);

  @Autowired
  private StorageService storageService;
  @Autowired
  private SchemaCrawlerService scSqliteService;

  @ExceptionHandler(Throwable.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public String exception(final Throwable throwable, final Model model)
  {
    logger.error(throwable.getMessage(), throwable);

    final String errorMessage = throwable != null? throwable.getMessage()
                                                 : "Unknown error";
    model.addAttribute("errorMessage", errorMessage);

    return "error";
  }

  @GetMapping(value = "/")
  public String index()
  {
    return "redirect:/schemacrawler";
  }

  @GetMapping(value = "/schemacrawler/diagrams/images/{key}")
  public HttpEntity schemacrawlerSQLiteDiagram(final HttpServletResponse response,
                                               @PathVariable final String key)
    throws Exception
  {
    response.setContentType(MediaType.IMAGE_PNG_VALUE);
    try (final InputStream inputStream = storageService.stream(key, "png");
        final ServletOutputStream outputStream = response.getOutputStream();)
    {
      StreamUtils.copy(inputStream, outputStream);
    }
    return new ResponseEntity(HttpStatus.OK);
  }

  @GetMapping("/schemacrawler")
  public String schemacrawlerSQLiteDiagramForm(final Model model)
  {
    model.addAttribute("diagramRequest", new SchemaCrawlerDiagramRequest());
    return "SchemaCrawlerSQLiteDiagramForm";
  }

  // http://stackoverflow.com/questions/30297719/cannot-get-validation-working-with-spring-boot-and-thymeleaf
  @PostMapping(value = "/schemacrawler")
  public String schemacrawlerSQLiteDiagramFormSubmit(@ModelAttribute("diagramRequest") @Valid final SchemaCrawlerDiagramRequest diagramRequest,
                                                     final BindingResult bindingResult,
                                                     @RequestParam("file") final MultipartFile file)
    throws Exception
  {
    if (bindingResult.hasErrors())
    {
      return "SchemaCrawlerSQLiteDiagramForm";
    }

    generateSchemaCrawlerSQLiteDiagram(diagramRequest, file);

    return "SchemaCrawlerSQLiteDiagramResult";
  }

  @GetMapping(value = "/schemacrawler/diagrams/{key}")
  public String schemacrawlerSQLiteDiagramPage(final Model model,
                                               @PathVariable final String key)
    throws Exception
  {
    final Path jsonFile = storageService.resolve(key, "json")
      .orElseThrow(() -> new Exception("Cannot find diagram for " + key));
    final SchemaCrawlerDiagramRequest diagramRequest = SchemaCrawlerDiagramRequest
      .fromJson(new String(Files.readAllBytes(jsonFile)));
    model.addAttribute("diagramRequest", diagramRequest);

    return "SchemaCrawlerSQLiteDiagram";
  }

  private void generateSchemaCrawlerSQLiteDiagram(final SchemaCrawlerDiagramRequest diagramRequest,
                                                  final MultipartFile file)
    throws Exception
  {
    final String DATABASE_EXT = "db";
    final String DIAGRAM_EXT = "png";

    final String key = diagramRequest.getKey();

    // Store the uploaded SQLite database file
    storageService.store(file, key, DATABASE_EXT);

    // Generate a database diagram, and store the generated image
    final Path dbFile = storageService.resolve(key, DATABASE_EXT).get();
    final Path schemaCrawlerDiagram = scSqliteService
      .createSchemaCrawlerDiagram(dbFile, DIAGRAM_EXT);
    storageService.store(schemaCrawlerDiagram, key, DIAGRAM_EXT);

    // Save the JSON request to disk
    storageService.store(new ByteArrayInputStream(diagramRequest.toString()
      .getBytes(UTF_8)), key, "json");
  }

}
