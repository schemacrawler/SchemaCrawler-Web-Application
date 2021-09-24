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
package us.fatehi.schemacrawler.webapp.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;
import static us.fatehi.schemacrawler.webapp.test.S3ServiceControllerTestConfig.TEST_SC_WEB_APP_BUCKET;

import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Object;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    properties = {
      "spring.main.allow-bean-definition-overriding=true",
      "AWS_ACCESS_KEY_ID=no-access-key",
      "AWS_SECRET=no-secret",
      "AWS_S3_BUCKET=no-bucket"
    })
@AutoConfigureMockMvc
@ActiveProfiles("production")
@SpringJUnitConfig(S3ServiceControllerTestConfig.class)
@Testcontainers(disabledWithoutDocker = true)
public class S3ServiceControllerTest {

  private static final DockerImageName localstackImage =
      DockerImageName.parse("localstack/localstack").withTag("0.12.18");

  public static final LocalStackContainer localstack =
      new LocalStackContainer(localstackImage).withServices(S3);

  static {
    localstack.start();
    createS3Bucket();
  }

  private static void createS3Bucket() {
    final S3Client s3Client = new S3ServiceControllerTestConfig().s3Client();
    s3Client.createBucket(b -> b.bucket(TEST_SC_WEB_APP_BUCKET));
    s3Client.waiter().waitUntilBucketExists(b -> b.bucket(TEST_SC_WEB_APP_BUCKET));
  }

  @Autowired private MockMvc mvc;

  @Test
  public void formWithUpload() throws Exception {

    final MockMultipartFile multipartFile =
        new MockMultipartFile(
            "file", "test.db", "application/octet-stream", RandomUtils.nextBytes(5));

    mvc.perform(
            multipart("/schemacrawler")
                .file(multipartFile)
                .param("name", "Sualeh")
                .param("email", "sualeh@hotmail.com"))
        .andExpect(view().name("SchemaCrawlerDiagramResult"))
        .andExpect(status().is2xxSuccessful());

    final S3Client s3Client = new S3ServiceControllerTestConfig().s3Client();
    final List<S3Object> contents =
        s3Client.listObjects(b -> b.bucket(TEST_SC_WEB_APP_BUCKET)).contents();
    assertThat(contents.size(), is(greaterThan(0)));
    assertThat(contents.get(0).key(), matchesPattern("[a-z0-9]{12}.db"));
    assertThat(contents.get(0).size(), is(5L));
  }
}
