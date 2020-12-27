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
import static java.nio.file.Files.newBufferedReader;
import static us.fatehi.schemacrawler.webapp.service.storage.FileExtensionType.JSON;
import static us.fatehi.schemacrawler.webapp.service.storage.FileExtensionType.PNG;
import static us.fatehi.schemacrawler.webapp.service.storage.FileExtensionType.SQLITE_DB;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import us.fatehi.schemacrawler.webapp.model.SchemaCrawlerDiagramRequest;
import us.fatehi.schemacrawler.webapp.service.processing.ProcessingService;
import us.fatehi.schemacrawler.webapp.service.storage.StorageService;

@Controller
public class SchemaCrawlerDiagramController
{

  private final StorageService storageService;
  private final ProcessingService processingService;

  @Autowired
  public SchemaCrawlerDiagramController(
    @NotNull final StorageService storageService,
    @NotNull final ProcessingService processingService)
  {
    this.storageService = storageService;
    this.processingService = processingService;
  }

  @GetMapping(value = "/")
  public String index()
  {
    return "redirect:/schemacrawler";
  }

  @ResponseBody
  @GetMapping(value = "/schemacrawler/images/{key}",
              produces = MediaType.IMAGE_PNG_VALUE)
  public Resource diagramImage(
    @PathVariable @NotNull @Pattern(regexp = "[A-Za-z0-9]{12}")
    @Size(min = 12, max = 12) final String key)
    throws Exception
  {
    return storageService
      .retrieveLocal(key, PNG)
      .map(PathResource::new)
      .orElseThrow(() -> new Exception("Cannot find image, " + key));
  }

  @GetMapping("/schemacrawler")
  public String diagramRequestForm(@NotNull final Model model)
  {
    model.addAttribute("diagramRequest", new SchemaCrawlerDiagramRequest());
    return "SchemaCrawlerDiagramForm";
  }

  // http://stackoverflow.com/questions/30297719/cannot-get-validation-working-with-spring-boot-and-thymeleaf
  @PostMapping(value = "/schemacrawler")
  public String diagramRequestFormSubmit(
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

    final String key = diagramRequest.getKey();

    // Store the uploaded database file
    final Path localPath = storageService.storeLocal(file, key, SQLITE_DB);

    // Make asynchronous call to generate diagram
    processingService.generateSchemaCrawlerDiagram(diagramRequest, localPath);

    return "SchemaCrawlerDiagramResult";
  }

  @GetMapping(value = "/schemacrawler/{key}")
  public String retrieveResults(final Model model,
                                @PathVariable @NotNull
                                @Pattern(regexp = "[A-Za-z0-9]{12}")
                                @Size(min = 12, max = 12) final String key)
    throws Exception
  {
    final Path jsonFile = storageService
      .retrieveLocal(key, JSON)
      .orElseThrow(() -> new Exception("Cannot find request for " + key));
    final SchemaCrawlerDiagramRequest diagramRequest =
      SchemaCrawlerDiagramRequest.fromJson(newBufferedReader(jsonFile, UTF_8));
    model.addAttribute("diagramRequest", diagramRequest);

    return "SchemaCrawlerDiagram";
  }

}
