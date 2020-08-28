package us.fatehi.schemacrawler.webapp.service.storage;


import java.nio.file.Path;

import org.springframework.context.annotation.Bean;

public interface FileSystemStorageConfig
{
  @Bean("fileSystemStorageRootPath")
  Path fileSystemStorageRootPath();
}
