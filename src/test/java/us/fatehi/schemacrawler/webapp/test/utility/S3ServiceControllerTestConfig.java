/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
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
package us.fatehi.schemacrawler.webapp.test.utility;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.services.s3.S3Client;
import us.fatehi.schemacrawler.webapp.test.RequestControllerWithS3Test;

@TestConfiguration
public class S3ServiceControllerTestConfig {

  public static final String TEST_SC_WEB_APP_BUCKET = "test-sc-web-app-bucket";

  @Bean(name = "s3Bucket")
  public String awsS3Bucket() {
    return TEST_SC_WEB_APP_BUCKET;
  }

  @Bean(name = "s3Client")
  public S3Client s3Client() {
    return LocalStackTestUtility.newS3Client(RequestControllerWithS3Test.localstack);
  }
}
