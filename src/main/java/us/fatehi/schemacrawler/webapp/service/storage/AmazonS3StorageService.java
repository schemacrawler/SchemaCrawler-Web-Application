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
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.InputStreamSource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

@Service("amazonS3StorageService")
@Profile("production")
public class AmazonS3StorageService implements StorageService {

  private static final Logger logger = Logger.getLogger(AmazonS3StorageService.class.getName());

  private final String awsS3Bucket;
  private final AmazonS3 amazonS3;

  @Autowired
  public AmazonS3StorageService(
      @NonNull final Region awsRegion,
      @NonNull final String awsS3Bucket,
      @NonNull final AWSCredentialsProvider awsCredentialsProvider) {
    this.amazonS3 =
        AmazonS3ClientBuilder.standard()
            .withCredentials(awsCredentialsProvider)
            .withRegion(awsRegion.getName())
            .build();
    this.awsS3Bucket = awsS3Bucket;
  }

  @Override
  @PostConstruct
  public void init() {}

  /** {@inheritDoc} */
  @Override
  public Optional<Path> retrieveLocal(final String key, final FileExtensionType extension)
      throws Exception {
    validateKey(key);

    if (extension == null) {
      return Optional.empty();
    }
    final Path filePath;

    try {
      // Download file from S3
      filePath = Files.createTempFile("sc-webapp", "." + extension.getExtension());
      final GetObjectRequest request =
          new GetObjectRequest(awsS3Bucket, key + "." + extension.getExtension());
      final S3Object s3Object = amazonS3.getObject(request);
      copy(s3Object.getObjectContent(), filePath, REPLACE_EXISTING);
    } catch (final Exception e) {
      logger.log(Level.WARNING, String.format("Could not retrieve, %s.%s", key, extension), e);
      return Optional.empty();
    }

    return Optional.of(filePath);
  }

  /** {@inheritDoc} */
  @Override
  public void store(
      @NonNull final InputStreamSource streamSource,
      @NonNull final String key,
      @NonNull final FileExtensionType extension)
      throws Exception {
    validateKey(key);

    try {
      // Save stream to a S3
      final ObjectMetadata metadata = new ObjectMetadata();
      metadata.setContentType(extension.getMimeType());
      final PutObjectRequest request =
          new PutObjectRequest(
              awsS3Bucket,
              key + "." + extension.getExtension(),
              streamSource.getInputStream(),
              metadata);
      amazonS3.putObject(request);
    } catch (final Exception e) {
      logger.log(Level.WARNING, String.format("Could not store, %s.%s", key, extension), e);
    }
  }

  /** {@inheritDoc} */
  @Override
  public Path storeLocal(
      @NonNull final InputStreamSource streamSource,
      @NonNull final String key,
      @NonNull final FileExtensionType extension)
      throws Exception {
    validateKey(key);

    // Save stream to a file
    final Path filePath = Files.createTempFile("sc-webapp.", "." + extension.getExtension());
    copy(streamSource.getInputStream(), filePath, REPLACE_EXISTING);

    // Check that the file is not empty
    if (Files.size(filePath) == 0) {
      Files.delete(filePath);
      throw new Exception(String.format("No data for file %s.%s", key, extension));
    }

    return filePath;
  }

  /**
   * Prevent malicious injection attacks.
   *
   * @param key Key
   * @throws Exception On a badly constructed key.
   */
  private void validateKey(final String key) throws Exception {
    if (StringUtils.length(key) != 12 || !StringUtils.isAlphanumeric(key)) {
      throw new Exception(String.format("Invalid filename key, %s", key));
    }
  }
}
