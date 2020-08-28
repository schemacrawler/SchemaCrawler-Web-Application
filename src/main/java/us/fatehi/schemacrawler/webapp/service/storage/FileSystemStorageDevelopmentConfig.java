package us.fatehi.schemacrawler.webapp.service.storage;


import javax.validation.constraints.NotNull;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

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
