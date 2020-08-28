package us.fatehi.schemacrawler.webapp.service.storage;


import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("development")
public class FileSystemStorageDevelopmentConfig
  implements FileSystemStorageConfig
{

  @Override
  @Bean("fileSystemStorageRootPath")
  public Path fileSystemStorageRootPath()
  {
    return Paths
      .get("./target/sc-webapp-storage")
      .normalize()
      .toAbsolutePath();
  }

}
