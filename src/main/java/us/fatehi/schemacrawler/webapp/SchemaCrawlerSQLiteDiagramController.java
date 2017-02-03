package us.fatehi.schemacrawler.webapp;


import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.sql.Connection;

import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

  @PostMapping(value = "/schemacrawler", produces = MediaType.IMAGE_PNG_VALUE)
  public ResponseEntity<byte[]> schemacrawlerSQLiteDiagramFormSubmit(@ModelAttribute("diagramRequest") @Valid final SchemaCrawlerSQLiteDiagramRequest diagramRequest,
                                                                     @RequestParam("file") final MultipartFile file,
                                                                     final BindingResult bindingResult)
    throws Exception
  {

    if (bindingResult.hasErrors())
    {
      // return "SchemaCrawlerSQLiteDiagramForm";
      throw new Exception();
    }

    final Path serverLocalPath = storageService.store(file);
    final Connection connection = schemacrawlerService
      .createDatabaseConnection(serverLocalPath);
    final Path schemaCrawlerDiagram = schemacrawlerService
      .createSchemaCrawlerDiagram(connection);
    System.out.println(schemaCrawlerDiagram);

    // return "SchemaCrawlerSQLiteDiagram";

    final HttpHeaders headers = new HttpHeaders();
    final InputStream in = new FileInputStream(schemaCrawlerDiagram.toFile());
    final byte[] media = IOUtils.toByteArray(in);
    headers.setCacheControl(CacheControl.noCache().getHeaderValue());

    final ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(media,
                                                                       headers,
                                                                       HttpStatus.OK);
    return responseEntity;
  }

}
