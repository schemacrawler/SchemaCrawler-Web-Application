package us.fatehi.schemacrawler.webapp.storage;


import static java.nio.file.Files.copy;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.isReadable;
import static java.nio.file.Files.isRegularFile;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
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
      if (!exists(rootLocation))
      {
        createDirectories(rootLocation);
      }
    }
    catch (final IOException e)
    {
      throw new StorageException("Could not initialize storage", e);
    }
  }

  @Override
  public Optional<Path> resolve(final String filenameKey)
    throws Exception
  {
    if (StringUtils.isBlank(filenameKey))
    {
      return Optional.ofNullable(null);
    }
    final Path serverLocalPath = rootLocation.resolve(filenameKey);
    if (!exists(serverLocalPath) || !isRegularFile(serverLocalPath)
        || !isReadable(serverLocalPath))
    {
      return Optional.ofNullable(null);
    }
    return Optional.of(serverLocalPath);
  }

  @Override
  public String store(final MultipartFile file)
  {
    try
    {
      if (file.isEmpty())
      {
        throw new StorageException("Failed to store empty file "
                                   + file.getOriginalFilename());
      }
      final String filenameKey = RandomStringUtils.randomAlphanumeric(12)
        .toLowerCase();
      final Path serverLocalPath = rootLocation.resolve(filenameKey);
      copy(file.getInputStream(), serverLocalPath);
      return filenameKey;
    }
    catch (final IOException e)
    {
      throw new StorageException("Failed to store file "
                                 + file.getOriginalFilename(), e);
    }
  }

  @Override
  public InputStream stream(final String filenameKey)
    throws Exception
  {
    final Optional<Path> serverLocalPath = resolve(filenameKey);
    if (serverLocalPath.isPresent())
    {
      return new FileInputStream(serverLocalPath.get().toFile());
    }
    else
    {
      return new ByteArrayInputStream(new byte[0]);
    }
  }

}
