/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.schemacrawler.webapp.test.utility;

import java.net.URI;
import org.testcontainers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

public final class LocalStackTestUtility {

  public static LocalStackContainer newLocalStackContainer() {
    final DockerImageName localstackImage =
        DockerImageName.parse("localstack/localstack").withTag("4.10");

    final LocalStackContainer localstack =
        new LocalStackContainer(localstackImage).withServices("s3");

    return localstack;
  }

  public static S3Client newS3Client(final LocalStackContainer localstack) {
    final URI s3Endpoint =
        URI.create("http://" + localstack.getHost() + ":" + localstack.getMappedPort(4566));

    final S3Client s3 =
        S3Client.builder()
            .endpointOverride(s3Endpoint)
            // LocalStack requires path-style access
            .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
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
