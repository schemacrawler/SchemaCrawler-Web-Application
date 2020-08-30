package us.fatehi.schemacrawler.webapp.service.storage;


import javax.validation.constraints.NotNull;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileSystemStorageConfig
{

  @Value("${SC_WEBAPP_STORAGE:./target/sc-webapp-storage}")
  @NotNull
  private Path fileSystemStorageRootPath;

  @Bean("fileSystemStorageRootPath")
  public Path fileSystemStorageRootPath()
  {
    return fileSystemStorageRootPath
      .normalize()
      .toAbsolutePath();
  }

}
