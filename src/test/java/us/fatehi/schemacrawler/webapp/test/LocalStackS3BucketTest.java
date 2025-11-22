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

import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.localstack.LocalStackContainer;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;
import us.fatehi.schemacrawler.webapp.test.utility.LocalStackTestUtility;

@Testcontainers(disabledWithoutDocker = true)
public class LocalStackS3BucketTest {

  private static final String TEST_CONTENT = "baz";
  private static final String TEST_OBJECT_NAME = "bar";
  private static final String TEST_BUCKET_NAME = "foo";

  @Container
  private final LocalStackContainer localstack =
      LocalStackTestUtility.newLocalStackContainerContainer();

  @Test
  public void createS3Object() {
    final S3Client s3Client = LocalStackTestUtility.newS3Client(localstack);

    s3Client.createBucket(b -> b.bucket(TEST_BUCKET_NAME));
    s3Client.waiter().waitUntilBucketExists(b -> b.bucket(TEST_BUCKET_NAME));
    s3Client.putObject(
        b -> b.bucket(TEST_BUCKET_NAME).key(TEST_OBJECT_NAME),
        RequestBody.fromBytes(TEST_CONTENT.getBytes()));
    s3Client.waiter().waitUntilObjectExists(b -> b.bucket(TEST_BUCKET_NAME).key(TEST_OBJECT_NAME));

    final HeadBucketResponse headBucketResponse =
        s3Client.headBucket(b -> b.bucket(TEST_BUCKET_NAME));

    assertThat(headBucketResponse.sdkHttpResponse().isSuccessful(), is(true));

    final ResponseBytes<GetObjectResponse> objectAsBytes =
        s3Client.getObjectAsBytes(b -> b.bucket(TEST_BUCKET_NAME).key(TEST_OBJECT_NAME));
    final String data = new String(objectAsBytes.asByteArray());

    assertThat(data, is(TEST_CONTENT));
  }
}
