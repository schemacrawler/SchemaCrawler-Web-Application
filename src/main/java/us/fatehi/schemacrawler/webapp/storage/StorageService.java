package us.fatehi.schemacrawler.webapp.storage;


import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService
{

  void init();

  Optional<Path> resolve(String filenameKey, String extension)
    throws Exception;

  String store(MultipartFile file, String extension)
    throws Exception;

  String store(Path file, String filenameKey)
    throws Exception;

  InputStream stream(String filenameKey, String extension)
    throws Exception;

}
