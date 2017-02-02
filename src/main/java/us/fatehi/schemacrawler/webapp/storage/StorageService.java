package us.fatehi.schemacrawler.webapp.storage;


import org.springframework.web.multipart.MultipartFile;

public interface StorageService
{

  void init();

  void store(MultipartFile file);

}
