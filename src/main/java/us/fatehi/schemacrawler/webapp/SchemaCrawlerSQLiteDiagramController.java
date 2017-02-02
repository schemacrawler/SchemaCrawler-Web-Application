package us.fatehi.schemacrawler.webapp;


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
import us.fatehi.schemacrawler.webapp.storage.StorageService;

@Controller
public class SchemaCrawlerSQLiteDiagramController
{

  private final StorageService storageService;

  @Autowired
  public SchemaCrawlerSQLiteDiagramController(final StorageService storageService)
  {
    this.storageService = storageService;
  }

  @GetMapping("/schemacrawler")
  public String schemacrawlerSQLiteDiagramForm(final Model model)
  {
    model.addAttribute("diagramRequest",
                       new SchemaCrawlerSQLiteDiagramRequest());
    return "SchemaCrawlerSQLiteDiagramForm";
  }

  @PostMapping("/schemacrawler")
  public String schemacrawlerSQLiteDiagramFormSubmit(@ModelAttribute("diagramRequest") @Valid final SchemaCrawlerSQLiteDiagramRequest diagramRequest,
                                                     @RequestParam("file") final MultipartFile file,
                                                     final BindingResult bindingResult)
  {

    if (bindingResult.hasErrors())
    {
      return "SchemaCrawlerSQLiteDiagramForm";
    }

    storageService.store(file);
    
    return "SchemaCrawlerSQLiteDiagram";
  }

}
