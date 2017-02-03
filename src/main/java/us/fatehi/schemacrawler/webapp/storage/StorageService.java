package us.fatehi.schemacrawler.webapp.storage;


import java.nio.file.Path;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService
{

  void init();

  Path store(MultipartFile file);

}
