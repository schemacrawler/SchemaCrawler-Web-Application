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
package us.fatehi.schemacrawler.webapp.service.storage;


import static java.nio.file.Files.copy;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.isReadable;
import static java.nio.file.Files.isRegularFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Service;

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

    storageRoot = Paths.get(storageRootPath).normalize().toAbsolutePath();

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
    validateFilenameKey(filenameKey);
    if (extension == null)
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
  public void store(final InputStreamSource streamSource,
                    final String filenameKey,
                    final FileExtensionType extension)
    throws Exception
  {
    validateFilenameKey(filenameKey);
    if (streamSource == null || extension == null)
    {
      throw new Exception(String.format("Failed to store file %s%s",
                                        filenameKey,
                                        extension));
    }

    // Save stream to a file
    final Path filePath = storageRoot
      .resolve(filenameKey + "." + extension.getExtension());
    copy(streamSource.getInputStream(), filePath);

    // Check that the file is not empty
    if (Files.size(filePath) == 0)
    {
      Files.delete(filePath);
      throw new Exception(String.format("No data for file %s.%s",
                                        filenameKey,
                                        extension));
    }
  }

  /**
   * Prevent malicious injection attacks.
   *
   * @param filenameKey
   *        Filename key
   * @throws Exception
   *         On a badly constructed filename key.
   */
  private void validateFilenameKey(final String filenameKey)
    throws Exception
  {
    if (StringUtils.length(filenameKey) != 12
        || !StringUtils.isAlphanumeric(filenameKey))
    {
      throw new Exception(String.format("Invalid filename key \"%s\"",
                                        filenameKey));
    }
  }

}
