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
package us.fatehi.schemacrawler.webapp.controller;


import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.IOUtils.toInputStream;
import static us.fatehi.schemacrawler.webapp.service.storage.FileExtensionType.JSON;
import static us.fatehi.schemacrawler.webapp.service.storage.FileExtensionType.PNG;
import static us.fatehi.schemacrawler.webapp.service.storage.FileExtensionType.SQLITE_DB;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.PathResource;
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

  private static final Logger logger =
    LoggerFactory.getLogger(SchemaCrawlerDiagramController.class);

  private final StorageService storageService;
  private final SchemaCrawlerService scService;

  @Autowired
  public SchemaCrawlerDiagramController(
    @NotNull final StorageService storageService,
    @NotNull final SchemaCrawlerService scService)
  {
    this.storageService = storageService;
    this.scService = scService;
  }

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
  @GetMapping(value = "/schemacrawler/images/{key}",
              produces = MediaType.IMAGE_PNG_VALUE)
  public Resource schemacrawlerDiagram(
    @PathVariable @NotNull @Pattern(regexp = "[A-Za-z0-9]{12}")
    @Size(min = 12, max = 12) final String key)
    throws Exception
  {
    return storageService
      .resolve(key, PNG)
      .map(path -> new PathResource(path))
      .orElseThrow(() -> new Exception("Cannot find image, " + key));
  }

  @GetMapping("/schemacrawler")
  public String schemacrawlerDiagramForm(@NotNull final Model model)
  {
    model.addAttribute("diagramRequest", new SchemaCrawlerDiagramRequest());
    return "SchemaCrawlerDiagramForm";
  }

  // http://stackoverflow.com/questions/30297719/cannot-get-validation-working-with-spring-boot-and-thymeleaf
  @PostMapping(value = "/schemacrawler")
  public String schemacrawlerDiagramFormSubmit(
    @ModelAttribute("diagramRequest") @NotNull @Valid
    final SchemaCrawlerDiagramRequest diagramRequest,
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
                                         @PathVariable @NotNull
                                         @Pattern(regexp = "[A-Za-z0-9]{12}")
                                         @Size(min = 12, max = 12)
                                         final String key)
    throws Exception
  {
    final Path jsonFile = storageService
      .resolve(key, JSON)
      .orElseThrow(() -> new Exception("Cannot find integration for " + key));
    final SchemaCrawlerDiagramRequest diagramRequest =
      SchemaCrawlerDiagramRequest.fromJson(new String(Files.readAllBytes(
        jsonFile)));
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

    // Generate a database integration, and store the generated image
    final Path dbFile = storageService
      .resolve(key, SQLITE_DB)
      .orElseThrow(() -> new Exception(String.format(
        "Cannot locate database file, %s",
        key)));
    final Path schemaCrawlerDiagram =
      scService.createSchemaCrawlerDiagram(dbFile, PNG.getExtension());
    storageService.store(new PathResource(schemaCrawlerDiagram), key, PNG);

    // Save the JSON request to disk
    storageService.store(new InputStreamResource(toInputStream(diagramRequest.toString(),
                                                               UTF_8)),
                         key,
                         JSON);
  }
}
