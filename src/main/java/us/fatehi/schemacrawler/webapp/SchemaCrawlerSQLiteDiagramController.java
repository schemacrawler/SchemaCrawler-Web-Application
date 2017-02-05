package us.fatehi.schemacrawler.webapp;


import java.nio.file.Path;
import java.sql.Connection;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import us.fatehi.schemacrawler.webapp.model.SchemaCrawlerSQLiteDiagramRequest;
import us.fatehi.schemacrawler.webapp.schemacrawler.SchemaCrawlerService;
import us.fatehi.schemacrawler.webapp.storage.StorageService;

@Controller
public class SchemaCrawlerSQLiteDiagramController
{

  private final StorageService storageService;
  private final SchemaCrawlerService schemacrawlerService;

  @Autowired
  public SchemaCrawlerSQLiteDiagramController(final StorageService storageService,
                                              final SchemaCrawlerService schemacrawlerService)
  {
    this.storageService = storageService;
    this.schemacrawlerService = schemacrawlerService;
  }

  @GetMapping("/schemacrawler")
  public String schemacrawlerSQLiteDiagramForm(final Model model)
  {
    model.addAttribute("diagramRequest",
                       new SchemaCrawlerSQLiteDiagramRequest());
    return "SchemaCrawlerSQLiteDiagramForm";
  }

  @PostMapping(value = "/schemacrawler")
  public String schemacrawlerSQLiteDiagramFormSubmit(@ModelAttribute("diagramRequest") @Valid final SchemaCrawlerSQLiteDiagramRequest diagramRequest,
                                                     @RequestParam("file") final MultipartFile file,
                                                     final BindingResult bindingResult)
    throws Exception
  {

    if (bindingResult.hasErrors())
    {
      return "SchemaCrawlerSQLiteDiagramForm";
    }

    final String filenameKey = storageService.store(file);
    final Connection connection = schemacrawlerService
      .createDatabaseConnection(storageService.resolve(filenameKey).get());
    final Path schemaCrawlerDiagram = schemacrawlerService
      .createSchemaCrawlerDiagram(connection);
    System.out.println(schemaCrawlerDiagram);

    return "SchemaCrawlerSQLiteDiagram";
  }

}
