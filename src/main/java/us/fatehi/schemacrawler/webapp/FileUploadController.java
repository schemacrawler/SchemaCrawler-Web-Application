package us.fatehi.schemacrawler.webapp;


import java.io.IOException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import us.fatehi.schemacrawler.webapp.storage.StorageFileNotFoundException;
import us.fatehi.schemacrawler.webapp.storage.StorageService;

@Controller
public class FileUploadController
{

  private final StorageService storageService;

  @Autowired
  public FileUploadController(final StorageService storageService)
  {
    this.storageService = storageService;
  }

  @PostMapping("/")
  public String handleFileUpload(@RequestParam("file") final MultipartFile file,
                                 final RedirectAttributes redirectAttributes)
  {

    storageService.store(file);
    redirectAttributes.addFlashAttribute("message",
                                         "You successfully uploaded "
                                                    + file.getOriginalFilename()
                                                    + "!");

    return "redirect:/";
  }

  @ExceptionHandler(StorageFileNotFoundException.class)
  public ResponseEntity handleStorageFileNotFound(final StorageFileNotFoundException exc)
  {
    return ResponseEntity.notFound().build();
  }

  @GetMapping("/")
  public String listUploadedFiles(final Model model)
    throws IOException
  {

    model.addAttribute("files",
                       storageService.loadAll()
                         .map(path -> MvcUriComponentsBuilder
                           .fromMethodName(FileUploadController.class,
                                           "serveFile",
                                           path.getFileName().toString())
                           .build().toString())
                         .collect(Collectors.toList()));

    return "uploadForm";
  }

  @GetMapping("/files/{filename:.+}")
  @ResponseBody
  public ResponseEntity<Resource> serveFile(@PathVariable final String filename)
  {

    final Resource file = storageService.loadAsResource(filename);
    return ResponseEntity
      .ok().header(HttpHeaders.CONTENT_DISPOSITION,
                   "attachment; filename=\"" + file.getFilename() + "\"")
      .body(file);
  }

}
