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
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.isReadable;
import static java.nio.file.Files.isRegularFile;

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
                                final FileExtensionType extension)
    throws Exception
  {
    if (StringUtils.isBlank(filenameKey) || extension == null)
    {
      return Optional.ofNullable(null);
    }
    final Path serverLocalPath = storageRoot
      .resolve(filenameKey + "." + extension.getExtension());
    if (!exists(serverLocalPath) || !isRegularFile(serverLocalPath)
        || !isReadable(serverLocalPath))
    {
      return Optional.ofNullable(null);
    }
    return Optional.of(serverLocalPath);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void store(final InputStream stream,
                    final String filenameKey,
                    final FileExtensionType extension)
    throws IOException
  {
    if (stream == null || StringUtils.isBlank(filenameKey) || extension == null)
    {
      throw new IOException(String.format("Failed to store file %s%s",
                                          filenameKey,
                                          extension));
    }
    final Path filePath = storageRoot
      .resolve(filenameKey + "." + extension.getExtension());
    copy(stream, filePath);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void store(final MultipartFile file,
                    final String filenameKey,
                    final FileExtensionType extension)
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
                    final FileExtensionType extension)
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
  public InputStream stream(final String filenameKey,
                            final FileExtensionType extension)
    throws Exception
  {
    if (StringUtils.isBlank(filenameKey) || extension == null)
    {
      throw new IOException(String.format("Failed to stream file %s%s",
                                          filenameKey,
                                          extension));
    }
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

}
