/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@Profile("production")
public class AmazonS3StorageConfig {

  @Value("${AWS_ACCESS_KEY_ID}")
  @NotNull(message = "AWS_ACCESS_KEY_ID not provided")
  private String accessKeyId;

  @Value("${AWS_SECRET}")
  @NotNull(message = "AWS_SECRET not provided")
  private String secretAccessKey;

  @Value("${AWS_REGION:us-east-1}")
  @NotNull(message = "AWS_REGION not provided")
  private String awsRegion;

  @Value("${AWS_S3_BUCKET}")
  @NotNull(message = "AWS_S3_BUCKET not provided")
  private String s3Bucket;

  @Bean(name = "s3Bucket")
  public String s3Bucket() {
    if (StringUtils.isAnyBlank(s3Bucket)) {
      throw new InternalRuntimeException("No Amazon S3 bucket provided");
    }
    return s3Bucket;
  }

  @Bean(name = "s3Client")
  public S3Client s3Client() {
    final S3Client s3Client =
        S3Client.builder().credentialsProvider(awsCredentials()).region(awsRegion()).build();
    return s3Client;
  }

  private AwsCredentialsProvider awsCredentials() {
    if (StringUtils.isAnyBlank(accessKeyId, secretAccessKey)) {
      throw new InternalRuntimeException("No AWS credentials provided");
    }
    final AwsCredentials awsCredentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
    return StaticCredentialsProvider.create(awsCredentials);
  }

  private Region awsRegion() {
    if (StringUtils.isBlank(awsRegion)) {
      throw new InternalRuntimeException("No AWS region provided");
    }
    return Region.of(awsRegion);
  }
}
