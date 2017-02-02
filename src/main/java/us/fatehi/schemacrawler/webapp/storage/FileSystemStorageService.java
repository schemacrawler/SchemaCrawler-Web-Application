package us.fatehi.schemacrawler.webapp.storage;


import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileSystemStorageService
  implements StorageService
{

  private final Path rootLocation;

  public FileSystemStorageService() throws IOException
  {
    rootLocation = Paths.get("uploaded-files");
    if (!Files.exists(rootLocation)) {
      Files.createDirectories(rootLocation);
    }
  }

  @Override
  public void deleteAll()
  {
    FileSystemUtils.deleteRecursively(rootLocation.toFile());
  }

  @Override
  public void init()
  {
    try
    {
      Files.createDirectory(rootLocation);
    }
    catch (final IOException e)
    {
      throw new StorageException("Could not initialize storage", e);
    }
  }

  @Override
  public Path load(final String filename)
  {
    return rootLocation.resolve(filename);
  }

  @Override
  public Stream<Path> loadAll()
  {
    try
    {
      return Files.walk(rootLocation, 1)
        .filter(path -> !path.equals(rootLocation))
        .map(path -> rootLocation.relativize(path));
    }
    catch (final IOException e)
    {
      throw new StorageException("Failed to read stored files", e);
    }

  }

  @Override
  public Resource loadAsResource(final String filename)
  {
    try
    {
      final Path file = load(filename);
      final Resource resource = new UrlResource(file.toUri());
      if (resource.exists() || resource.isReadable())
      {
        return resource;
      }
      else
      {
        throw new StorageFileNotFoundException("Could not read file: "
                                               + filename);

      }
    }
    catch (final MalformedURLException e)
    {
      throw new StorageFileNotFoundException("Could not read file: " + filename,
                                             e);
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
