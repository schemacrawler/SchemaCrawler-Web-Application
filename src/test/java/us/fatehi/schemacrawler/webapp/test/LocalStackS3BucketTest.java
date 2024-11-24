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
package us.fatehi.schemacrawler.webapp.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;

@Testcontainers(disabledWithoutDocker = true)
public class LocalStackS3BucketTest {

  private static final String TEST_CONTENT = "baz";
  private static final String TEST_OBJECT_NAME = "bar";
  private static final String TEST_BUCKET_NAME = "foo";

  private final DockerImageName localstackImage =
      DockerImageName.parse("localstack/localstack").withTag("2.0.0");

  @Container
  private final LocalStackContainer localstack =
      new LocalStackContainer(localstackImage).withServices(S3);

  @Test
  public void createS3Object() {
    final S3Client s3 =
        S3Client.builder()
            .endpointOverride(localstack.getEndpointOverride(LocalStackContainer.Service.S3))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        localstack.getAccessKey(), localstack.getSecretKey())))
            .region(Region.of(localstack.getRegion()))
            .build();

    s3.createBucket(b -> b.bucket(TEST_BUCKET_NAME));
    s3.waiter().waitUntilBucketExists(b -> b.bucket(TEST_BUCKET_NAME));
    s3.putObject(
        b -> b.bucket(TEST_BUCKET_NAME).key(TEST_OBJECT_NAME),
        RequestBody.fromBytes(TEST_CONTENT.getBytes()));
    s3.waiter().waitUntilObjectExists(b -> b.bucket(TEST_BUCKET_NAME).key(TEST_OBJECT_NAME));

    final HeadBucketResponse headBucketResponse = s3.headBucket(b -> b.bucket(TEST_BUCKET_NAME));

    assertThat(headBucketResponse.sdkHttpResponse().isSuccessful(), is(true));

    final ResponseBytes<GetObjectResponse> objectAsBytes =
        s3.getObjectAsBytes(b -> b.bucket(TEST_BUCKET_NAME).key(TEST_OBJECT_NAME));
    final String data = new String(objectAsBytes.asByteArray());

    assertThat(data, is(TEST_CONTENT));
  }
}
