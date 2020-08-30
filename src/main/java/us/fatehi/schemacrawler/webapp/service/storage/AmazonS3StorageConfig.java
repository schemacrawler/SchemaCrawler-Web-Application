/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonS3StorageConfig
{

  @Value("${AWS_ACCESS_KEY_ID:bad-access-key}")
  @NotNull
  private String awsKeyId;

  @Value("${AWS_SECRET:bad-secret}")
  @NotNull
  private String awsKeySecret;

  @Value("${AWS_REGION:us-east-1}")
  @NotNull
  private String awsRegion;

  @Value("${AWS_S3_BUCKET:sc-web-app-1}")
  @NotNull
  private String awsS3Bucket;

  @Bean(name = "awsRegion")
  @NotNull
  public Region awsRegion()
  {
    if (StringUtils.isBlank(awsRegion))
    {
      throw new RuntimeException("No AWS region provided");
    }
    return Region.getRegion(Regions.fromName(awsRegion));
  }

  @Bean(name = "awsCredentials")
  @NotNull
  public AWSCredentialsProvider awsCredentials()
  {
    if (StringUtils.isAnyBlank(awsKeyId, awsKeySecret))
    {
      throw new RuntimeException("No AWS credentials provided");
    }
    final AWSCredentials awsCredentials =
      new BasicAWSCredentials(awsKeyId, awsKeySecret);
    return new AWSStaticCredentialsProvider(awsCredentials);
  }

  @Bean(name = "awsS3Bucket")
  public String awsS3Bucket()
  {
    return awsS3Bucket;
  }

}
