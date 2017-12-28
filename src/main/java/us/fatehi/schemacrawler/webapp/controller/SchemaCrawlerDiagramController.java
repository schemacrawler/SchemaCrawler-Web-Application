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
package us.fatehi.schemacrawler.webapp.controller;


import static java.nio.charset.StandardCharsets.UTF_8;
import static us.fatehi.schemacrawler.webapp.service.storage.FileExtensionType.JSON;
import static us.fatehi.schemacrawler.webapp.service.storage.FileExtensionType.PNG;
import static us.fatehi.schemacrawler.webapp.service.storage.FileExtensionType.SQLITE_DB;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.validation.Valid;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import us.fatehi.schemacrawler.webapp.model.SchemaCrawlerDiagramRequest;
import us.fatehi.schemacrawler.webapp.service.schemacrawler.SchemaCrawlerService;
import us.fatehi.schemacrawler.webapp.service.storage.StorageService;

@Controller
@ControllerAdvice
public class SchemaCrawlerDiagramController
{

  private static Logger logger = LoggerFactory
    .getLogger(SchemaCrawlerDiagramController.class);

  @Autowired
  private StorageService storageService;
  @Autowired
  private SchemaCrawlerService scService;

  @ExceptionHandler(Throwable.class)
  public String handleException(final Throwable throwable,
                                final RedirectAttributes redirectAttributes)
  {
    // See http://www.mkyong.com/spring-boot/spring-boot-file-upload-example/
    
    logger.error(throwable.getMessage(), throwable);

    final String errorMessage = ExceptionUtils.getRootCauseMessage(throwable);

    redirectAttributes.addFlashAttribute("errorMessage", errorMessage);

    return "redirect:error";
  }

  @GetMapping(value = "/")
  public String index()
  {
    return "redirect:/schemacrawler";
  }

  @ResponseBody
  @GetMapping(value = "/schemacrawler/images/{key}", produces = MediaType.IMAGE_PNG_VALUE)
  public Resource schemacrawlerDiagram(@PathVariable final String key)
    throws Exception
  {
    return storageService.resolve(key, PNG)
      .map(path -> new FileSystemResource(path.toFile()))
      .orElseThrow(() -> new Exception("Cannot find image /schemacrawler/images/"
                                       + key));
  }

  @GetMapping("/schemacrawler")
  public String schemacrawlerDiagramForm(final Model model)
  {
    model.addAttribute("diagramRequest", new SchemaCrawlerDiagramRequest());
    return "SchemaCrawlerDiagramForm";
  }

  // http://stackoverflow.com/questions/30297719/cannot-get-validation-working-with-spring-boot-and-thymeleaf
  @PostMapping(value = "/schemacrawler")
  public String schemacrawlerDiagramFormSubmit(@ModelAttribute("diagramRequest") @Valid final SchemaCrawlerDiagramRequest diagramRequest,
                                               final BindingResult bindingResult,
                                               @RequestParam("file") final MultipartFile file)
    throws Exception
  {
    if (bindingResult.hasErrors())
    {
      return "SchemaCrawlerDiagramForm";
    }

    generateSchemaCrawlerDiagram(diagramRequest, file);

    return "SchemaCrawlerDiagramResult";
  }

  @GetMapping(value = "/schemacrawler/{key}")
  public String schemacrawlerDiagramPage(final Model model,
                                         @PathVariable final String key)
    throws Exception
  {
    final Path jsonFile = storageService.resolve(key, JSON)
      .orElseThrow(() -> new Exception("Cannot find diagram for " + key));
    final SchemaCrawlerDiagramRequest diagramRequest = SchemaCrawlerDiagramRequest
      .fromJson(new String(Files.readAllBytes(jsonFile)));
    model.addAttribute("diagramRequest", diagramRequest);

    return "SchemaCrawlerDiagram";
  }

  private void generateSchemaCrawlerDiagram(final SchemaCrawlerDiagramRequest diagramRequest,
                                            final MultipartFile file)
    throws Exception
  {

    final String key = diagramRequest.getKey();

    // Store the uploaded database file
    storageService.store(file, key, SQLITE_DB);

    // Generate a database diagram, and store the generated image
    final Path dbFile = storageService.resolve(key, SQLITE_DB)
      .orElseThrow(() -> new Exception(String
        .format("Cannot locate database file %s.%s",
                key,
                SQLITE_DB)));
    final Path schemaCrawlerDiagram = scService
      .createSchemaCrawlerDiagram(dbFile, PNG.getExtension());
    storageService.store(new FileSystemResource(schemaCrawlerDiagram.toFile()),
                         key,
                         PNG);

    // Save the JSON request to disk
    storageService
      .store(new InputStreamResource(new ByteArrayInputStream(diagramRequest
        .toString().getBytes(UTF_8))), key, JSON);
  }

}
