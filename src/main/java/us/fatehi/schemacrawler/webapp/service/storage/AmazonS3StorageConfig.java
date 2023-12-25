/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

import jakarta.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@Profile("production")
public class AmazonS3StorageConfig {

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
  public S3Client s3Client(final AwsCredentialsProvider awsCredentials, final Region awsRegion) {
    final S3Client s3Client =
        S3Client.builder().credentialsProvider(awsCredentials).region(awsRegion).build();
    return s3Client;
  }
}
