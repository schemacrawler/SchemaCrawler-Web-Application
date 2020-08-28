package us.fatehi.schemacrawler.webapp.service.storage;


import java.nio.file.Path;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public interface FileSystemStorageConfig
{

  @Bean("fileSystemStorageRootPath")
  Path fileSystemStorageRootPath();

}
