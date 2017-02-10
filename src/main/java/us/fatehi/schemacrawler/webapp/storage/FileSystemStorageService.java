/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package us.fatehi.schemacrawler.webapp.storage;


import static java.nio.file.Files.copy;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.isReadable;
import static java.nio.file.Files.*;
import static java.util.Objects.requireNonNull;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service("storageService")
public class FileSystemStorageService
  implements StorageService
{

  @Value("${schemacrawler.webapp.storage-root}")
  private String storageRootPath;
  private Path storageRoot;

  @Override
  @PostConstruct
  public void init()
    throws Exception
  {
    if (StringUtils.isBlank(storageRootPath))
    {
      throw new Exception("'schemacrawler.webapp.storage-root' is not configured");
    }
    
    storageRoot = Paths.get(storageRootPath).toAbsolutePath();
    
    // Create storage root if it does not exist
    if (!exists(storageRoot))
    {
      createDirectories(storageRoot);
    }
    else if (!isDirectory(storageRoot))
    {
      throw new Exception("'schemacrawler.webapp.storage-root' is not a directory");
    }
  }

  /**
   * {@inheritDoc}
   */
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
    final Path serverLocalPath = storageRoot
      .resolve(filenameKey + filenameExt);
    if (!exists(serverLocalPath) || !isRegularFile(serverLocalPath)
        || !isReadable(serverLocalPath))
    {
      return Optional.ofNullable(null);
    }
    return Optional.of(serverLocalPath);
  }

  @Override
  public void store(final InputStream stream,
                    final String filenameKey,
                    final String extension)
    throws IOException
  {
    requireNonNull(stream);
    if (StringUtils.isBlank(filenameKey))
    {
      throw new IOException(String.format("Failed to store file %s%s",
                                          filenameKey,
                                          extension));
    }
    final String filenameExt = fixFilenameExtension(extension);
    final Path filePath = storageRoot.resolve(filenameKey + filenameExt);
    copy(stream, filePath);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void store(final MultipartFile file,
                    final String filenameKey,
                    final String extension)
    throws Exception
  {
    if (file == null || file.isEmpty())
    {
      throw new IOException("Failed to store empty file "
                            + file.getOriginalFilename());
    }

    store(file.getInputStream(), filenameKey, extension);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void store(final Path file,
                    final String filenameKey,
                    final String extension)
    throws Exception
  {
    if (file == null || !exists(file) || !isRegularFile(file)
        || !isReadable(file))
    {
      throw new IOException("Failed to store file " + file);
    }

    store(new FileInputStream(file.toFile()), filenameKey, extension);
  }

  /**
   * {@inheritDoc}
   */
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

  /**
   * Checks the extension, and prefixes with a dot.
   *
   * @param extension
   *        Extension prefixed with a dot.
   * @return Filename extension.
   */
  private String fixFilenameExtension(final String extension)
  {
    final String filenameExt = StringUtils
      .trimToNull(extension) == null? "": "." + StringUtils.trim(extension);
    return filenameExt.replaceAll("\\.\\.", ".");
  }

}
