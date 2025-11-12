/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.schemacrawler.webapp.test.utility;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public final class LocalStackTestUtility {

  public static LocalStackContainer newLocalStackContainerContainer() {
    final DockerImageName localstackImage =
        DockerImageName.parse("localstack/localstack").withTag("4.10");

    final LocalStackContainer localstack =
        new LocalStackContainer(localstackImage).withServices(S3);

    return localstack;
  }

  public static S3Client newS3Client(final LocalStackContainer localstack) {
    final S3Client s3 =
        S3Client.builder()
            .endpointOverride(localstack.getEndpointOverride(LocalStackContainer.Service.S3))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        localstack.getAccessKey(), localstack.getSecretKey())))
            .region(Region.of(localstack.getRegion()))
            .build();
    return s3;
  }

  private LocalStackTestUtility() {
    // Prevent instantiation
  }
}
