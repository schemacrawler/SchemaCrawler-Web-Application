/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

package us.fatehi.schemacrawler.webapp.test.service.storage;

import static java.nio.file.Files.copy;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.isReadable;
import static java.nio.file.Files.isRegularFile;

import jakarta.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Service;
import us.fatehi.schemacrawler.webapp.model.DiagramKey;
import us.fatehi.schemacrawler.webapp.service.storage.FileExtensionType;
import us.fatehi.schemacrawler.webapp.service.storage.StorageService;

@Service("fileSystemStorageService")
@Profile("local")
public class FileSystemStorageService implements StorageService {

  private final FileSystemStorageConfig config;

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
  public Optional<Path> retrieveLocal(final DiagramKey key, final FileExtensionType extension)
      throws Exception {
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
      @NonNull final DiagramKey key,
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
      @NonNull final DiagramKey key,
      @NonNull final FileExtensionType extension)
      throws Exception {
    final Path filePath =
        Path.of(
            System.getProperty("java.io.tmpdir"), "%s.%s".formatted(key, extension.getExtension()));

    saveFile(streamSource, key, extension, filePath);

    return filePath;
  }

  private void saveFile(
      final InputStreamSource streamSource,
      final DiagramKey key,
      final FileExtensionType extension,
      final Path filePath)
      throws Exception {

    // Save stream to a file
    copy(streamSource.getInputStream(), filePath);

    // Check that the file is not empty
    if (Files.size(filePath) == 0) {
      Files.delete(filePath);
      throw new Exception("Uploaded file has no data (%s)".formatted(key));
    }
  }
}
