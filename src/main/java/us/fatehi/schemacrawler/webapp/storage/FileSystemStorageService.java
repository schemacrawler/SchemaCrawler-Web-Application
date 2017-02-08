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

import org.apache.commons.io.FilenameUtils;
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
  public Optional<Path> resolve(final String filenameKey,
                                final String extension)
    throws Exception
  {
    if (StringUtils.isBlank(filenameKey))
    {
      return Optional.ofNullable(null);
    }
    final String filenameExt = fixFilenameExtension(extension);
    final Path serverLocalPath = rootLocation
      .resolve(filenameKey + filenameExt);
    if (!exists(serverLocalPath) || !isRegularFile(serverLocalPath)
        || !isReadable(serverLocalPath))
    {
      return Optional.ofNullable(null);
    }
    return Optional.of(serverLocalPath);
  }

  @Override
  public void store(final String filenameKey,
                    final MultipartFile file,
                    final String extension)
  {
    if (file == null || file.isEmpty())
    {
      throw new StorageException("Failed to store empty file "
                                 + file.getOriginalFilename());
    }
    try
    {
      store(file.getInputStream(), filenameKey, extension);
    }
    catch (final IOException e)
    {
      throw new StorageException("Failed to store file "
                                 + file.getOriginalFilename(), e);
    }
  }

  @Override
  public void store(final String fileNameKey, final Path file)
  {
    if (file == null || !exists(file) || !isRegularFile(file)
        || !isReadable(file))
    {
      throw new StorageException("Failed to store file " + file);
    }
    if (StringUtils.isBlank(fileNameKey))
    {
      throw new StorageException("Failed to store file " + file);
    }

    try
    {
      store(new FileInputStream(file.toFile()),
            fileNameKey,
            FilenameUtils.getExtension(file.toString()));
    }
    catch (final IOException e)
    {
      throw new StorageException("Failed to store file " + file, e);
    }
  }

  @Override
  public InputStream stream(final String filenameKey, final String extension)
    throws Exception
  {
    final Optional<Path> serverLocalPath = resolve(filenameKey, extension);
    if (serverLocalPath.isPresent())
    {
      return new FileInputStream(serverLocalPath.get().toFile());
    }
    else
    {
      return new ByteArrayInputStream(new byte[0]);
    }
  }

  private String fixFilenameExtension(final String extension)
  {
    final String filenameExt = StringUtils
      .trimToNull(extension) == null? ".dat": "." + StringUtils.trim(extension);
    return filenameExt;
  }

  private void store(final InputStream stream,
                     final String filenameKey,
                     final String extension)
    throws IOException
  {
    final String filenameExt = fixFilenameExtension(extension);
    final Path serverLocalPath = rootLocation
      .resolve(filenameKey + filenameExt);
    copy(stream, serverLocalPath);
  }

}
