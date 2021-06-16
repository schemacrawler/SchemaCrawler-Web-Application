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
import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.delete;
import static java.nio.file.Files.size;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.io.IOUtils.copy;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.InputStreamSource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import us.fatehi.schemacrawler.webapp.model.DiagramKey;

@Service("amazonS3StorageService")
@Profile("production")
public class AmazonS3StorageService implements StorageService {

  private static final Logger logger = Logger.getLogger(AmazonS3StorageService.class.getName());

  private final String awsS3Bucket;
  private final S3Client amazonS3;

  @Autowired
  public AmazonS3StorageService(
      @NonNull final Region awsRegion,
      @NonNull final String awsS3Bucket,
      @NonNull final AwsCredentialsProvider awsCredentialsProvider) {

    this.amazonS3 =
        S3Client.builder().region(awsRegion).credentialsProvider(awsCredentialsProvider).build();

    if (!bucketExists(awsS3Bucket)) {
      throw new IllegalArgumentException(
          String.format("AWS S3 bucket '%s' does not exist", awsS3Bucket));
    }
    this.awsS3Bucket = awsS3Bucket;
  }

  @Override
  @PostConstruct
  public void init() {}

  /** {@inheritDoc} */
  @Override
  public Optional<Path> retrieveLocal(final DiagramKey key, final FileExtensionType extension)
      throws Exception {

    if (extension == null) {
      return Optional.empty();
    }

    try {
      final String filename = key + "." + extension.getExtension();
      final Path tempFilePath = Files.createTempFile("sc-webapp", "." + extension.getExtension());

      // Download file from S3 to a local temporary file
      final GetObjectRequest request =
          GetObjectRequest.builder().bucket(awsS3Bucket).key(filename).build();
      try (final ResponseInputStream<GetObjectResponse> inputStream = amazonS3.getObject(request)) {
        copy(inputStream, tempFilePath, REPLACE_EXISTING);
      }

      return Optional.of(tempFilePath);

    } catch (final Exception e) {
      logger.log(Level.WARNING, String.format("Could not retrieve, %s.%s", key, extension), e);
      return Optional.empty();
    }
  }

  /** {@inheritDoc} */
  @Override
  public void store(
      @NonNull final InputStreamSource streamSource,
      @NonNull final DiagramKey key,
      @NonNull final FileExtensionType extension)
      throws Exception {

    try {

      // Save stream to a temporary file, so the AWS S3 API can get length of data and MD5 checksum,
      // and avoid ResetException
      final String filename = key + "." + extension.getExtension();
      final Path tempFilePath = createTempFile(null, filename).toAbsolutePath();
      try (final InputStream inputStream = streamSource.getInputStream();
          final OutputStream outputStream = new FileOutputStream(tempFilePath.toFile()); ) {
        copy(inputStream, outputStream);
      }

      // Upload local temporary file to S3
      final PutObjectRequest request =
          PutObjectRequest.builder().bucket(awsS3Bucket).key(filename).build();
      amazonS3.putObject(request, tempFilePath);

    } catch (final Exception e) {
      logger.log(Level.WARNING, String.format("Could not store, %s.%s", key, extension), e);
    }
  }

  /** {@inheritDoc} */
  @Override
  public Path storeLocal(
      @NonNull final InputStreamSource streamSource,
      @NonNull final DiagramKey key,
      @NonNull final FileExtensionType extension)
      throws Exception {

    // Save stream to a local temporary file
    final Path tempFilePath = createTempFile("sc-webapp.", "." + extension.getExtension());
    copy(streamSource.getInputStream(), tempFilePath, REPLACE_EXISTING);

    // Check that the file is not empty
    if (size(tempFilePath) == 0) {
      delete(tempFilePath);
      throw new Exception(String.format("No data for file %s.%s", key, extension));
    }

    return tempFilePath;
  }

  private boolean bucketExists(final String bucketName) {
    final HeadBucketRequest request = HeadBucketRequest.builder().bucket(bucketName).build();
    final HeadBucketResponse headBucketResponse = amazonS3.headBucket(request);
    return headBucketResponse.sdkHttpResponse().isSuccessful();
  }
}
