/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.InputStreamSource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service("fileSystemStorageService")
@Profile("development")
public class FileSystemStorageService implements StorageService {

  private final FileSystemStorageConfig config;

  @Autowired
  public FileSystemStorageService(@NonNull final FileSystemStorageConfig config) {
    this.config = config;
  }

  @Override
  @PostConstruct
  public void init() throws Exception {
    final Path storageRoot = config.fileSystemStorageRootPath();

    // Create storage root if it does not exist
    if (!exists(storageRoot)) {
      createDirectories(storageRoot);
    } else if (!isDirectory(storageRoot)) {
      throw new Exception("'schemacrawler.webapp.storage-root' is not a directory");
    }
  }

  /** {@inheritDoc} */
  @Override
  public Optional<Path> retrieveLocal(final String key, final FileExtensionType extension)
      throws Exception {
    validateKey(key);
    if (extension == null) {
      return Optional.empty();
    }
    final Path serverLocalPath =
        config.fileSystemStorageRootPath().resolve(key + "." + extension.getExtension());
    if (!exists(serverLocalPath)
        || !isRegularFile(serverLocalPath)
        || !isReadable(serverLocalPath)) {
      return Optional.empty();
    }
    return Optional.of(serverLocalPath);
  }

  /** {@inheritDoc} */
  @Override
  public void store(
      @NonNull final InputStreamSource streamSource,
      @NonNull final String key,
      @NonNull final FileExtensionType extension)
      throws Exception {
    final Path filePath =
        config.fileSystemStorageRootPath().resolve(key + "." + extension.getExtension());

    saveFile(streamSource, key, extension, filePath);
  }

  /** {@inheritDoc} */
  @Override
  public Path storeLocal(
      @NonNull final InputStreamSource streamSource,
      @NonNull final String key,
      @NonNull final FileExtensionType extension)
      throws Exception {
    final Path filePath =
        Paths.get(
            System.getProperty("java.io.tmpdir"),
            String.format("%s.%s", key, extension.getExtension()));

    saveFile(streamSource, key, extension, filePath);

    return filePath;
  }

  private void saveFile(
      final InputStreamSource streamSource,
      final String key,
      final FileExtensionType extension,
      final Path filePath)
      throws Exception {
    validateKey(key);

    // Save stream to a file
    copy(streamSource.getInputStream(), filePath);

    // Check that the file is not empty
    if (Files.size(filePath) == 0) {
      Files.delete(filePath);
      throw new Exception(String.format("No data for file %s.%s", key, extension));
    }
  }

  /**
   * Prevent malicious injection attacks.
   *
   * @param key Key
   * @throws Exception On a badly constructed key.
   */
  private void validateKey(final String key) throws Exception {
    if (StringUtils.length(key) != 12 || !StringUtils.isAlphanumeric(key)) {
      throw new Exception(String.format("Invalid filename key \"%s\"", key));
    }
  }
}
