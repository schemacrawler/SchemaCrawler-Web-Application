package us.fatehi.schemacrawler.webapp;


import java.io.InputStream;
import java.nio.file.Path;
import java.sql.Connection;

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

  @GetMapping(value = "/images/{key}")
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

    final String filenameKey = storageService.store(file, "db");
    final Connection connection = schemacrawlerService
      .createDatabaseConnection(storageService.resolve(filenameKey, "db")
        .get());
    final Path schemaCrawlerDiagram = schemacrawlerService
      .createSchemaCrawlerDiagram(connection);
    storageService.store(schemaCrawlerDiagram, filenameKey);
    diagramRequest.setKey(filenameKey);

    return "SchemaCrawlerSQLiteDiagram";
  }

}
