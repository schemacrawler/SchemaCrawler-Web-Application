package us.fatehi.schemacrawler.webapp.service.storage;


import javax.validation.constraints.NotNull;

import java.nio.file.Path;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile("production")
@PropertySource("classpath:/file-system-storage.properties")
public class FileSystemStorageProductionConfig
  implements FileSystemStorageConfig
{

  @Value("${file-system-storage.storage-root}")
  @NotNull
  private Path fileSystemStorageRootPath;

  @Override
  @Bean("fileSystemStorageRootPath")
  public Path fileSystemStorageRootPath()
  {
    return fileSystemStorageRootPath
      .normalize()
      .toAbsolutePath();
  }

}
