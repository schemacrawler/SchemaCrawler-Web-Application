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
import static org.apache.commons.io.FileUtils.writeStringToFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import us.fatehi.schemacrawler.webapp.model.SchemaCrawlerSQLiteDiagramRequest;
import us.fatehi.schemacrawler.webapp.repository.SchemaCrawlerSQLiteDiagramRequestRepository;
import us.fatehi.schemacrawler.webapp.schemacrawler.SchemaCrawlerService;
import us.fatehi.schemacrawler.webapp.storage.StorageService;

@Controller
public class SchemaCrawlerSQLiteDiagramController
{

  private final StorageService storageService;
  private final SchemaCrawlerService schemacrawlerService;
  private final SchemaCrawlerSQLiteDiagramRequestRepository requestRepository;

  @Autowired
  public SchemaCrawlerSQLiteDiagramController(final StorageService storageService,
                                              final SchemaCrawlerService schemacrawlerService,
                                              final SchemaCrawlerSQLiteDiagramRequestRepository requestRepository)
  {
    this.storageService = storageService;
    this.schemacrawlerService = schemacrawlerService;
    this.requestRepository = requestRepository;
  }

  @GetMapping(value = "/")
  public String index()
  {
    return "redirect:/schemacrawler";
  }

  @GetMapping(value = "/schemacrawler/diagrams/images/{key}")
  public HttpEntity schemacrawlerSQLiteDiagram(final HttpServletResponse response,
                                               @PathVariable final String key)
  {
    try (final InputStream inputStream = storageService.stream(key, "png");)
    {

      StreamUtils.copy(inputStream, response.getOutputStream());
      response.setContentType(MediaType.IMAGE_PNG_VALUE);
    }
    catch (final Exception e)
    {
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity(HttpStatus.OK);
  }

  @GetMapping("/schemacrawler")
  public String schemacrawlerSQLiteDiagramForm(final Model model)
  {
    model.addAttribute("diagramRequest",
                       new SchemaCrawlerSQLiteDiagramRequest());
    return "SchemaCrawlerSQLiteDiagramForm";
  }

  // http://stackoverflow.com/questions/30297719/cannot-get-validation-working-with-spring-boot-and-thymeleaf
  @PostMapping(value = "/schemacrawler")
  public String schemacrawlerSQLiteDiagramFormSubmit(@ModelAttribute("diagramRequest") @Valid final SchemaCrawlerSQLiteDiagramRequest diagramRequest,
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
  {

    final SchemaCrawlerSQLiteDiagramRequest diagramRequest = requestRepository
      .findByKey(key);
    model.addAttribute("diagramRequest", diagramRequest);

    return "SchemaCrawlerSQLiteDiagram";
  }

  private void generateSchemaCrawlerSQLiteDiagram(final SchemaCrawlerSQLiteDiagramRequest diagramRequest,
                                                  final MultipartFile file)
    throws Exception
  {
    final String filenameKey = diagramRequest.getKey();

    // Store the uploaded SQLite database file
    storageService.store(filenameKey, file, "db");

    // Generate a database diagram, and store the generated image
    final Path dbFile = storageService.resolve(filenameKey, "db").get();
    final Path schemaCrawlerDiagram = schemacrawlerService
      .createSchemaCrawlerDiagram(dbFile, "png");
    storageService.store(filenameKey, schemaCrawlerDiagram, "png");

    // Persist the request itself in the database
    requestRepository.save(diagramRequest);

    // Save the JSON request to disk, after the database id has been
    // generated
    final Path tempFile = Files.createTempFile("schemacrawler-web-application",
                                               ".json");
    writeStringToFile(tempFile.toFile(), diagramRequest.toString(), UTF_8);
    storageService.store(filenameKey, tempFile, "json");
  }

}
