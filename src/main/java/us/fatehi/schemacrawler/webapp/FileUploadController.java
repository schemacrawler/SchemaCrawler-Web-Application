package us.fatehi.schemacrawler.webapp;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

  @GetMapping("/")
  public String showFileUploadForm(final Model model)
  {
    return "uploadForm";
  }

}
