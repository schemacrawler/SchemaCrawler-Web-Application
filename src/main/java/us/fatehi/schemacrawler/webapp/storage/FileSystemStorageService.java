package us.fatehi.schemacrawler.webapp.storage;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileSystemStorageService
  implements StorageService
{

  private final Path rootLocation;

  public FileSystemStorageService()
    throws IOException
  {
    rootLocation = Paths.get("uploaded-files");
  }

  @Override
  public void init()
  {
    try
    {
      if (!Files.exists(rootLocation))
      {
        Files.createDirectories(rootLocation);
      }
    }
    catch (final IOException e)
    {
      throw new StorageException("Could not initialize storage", e);
    }
  }

  @Override
  public void store(final MultipartFile file)
  {
    try
    {
      if (file.isEmpty())
      {
        throw new StorageException("Failed to store empty file "
                                   + file.getOriginalFilename());
      }
      Files.copy(file.getInputStream(),
                 rootLocation.resolve(file.getOriginalFilename()));
    }
    catch (final IOException e)
    {
      throw new StorageException("Failed to store file "
                                 + file.getOriginalFilename(), e);
    }
  }
}
