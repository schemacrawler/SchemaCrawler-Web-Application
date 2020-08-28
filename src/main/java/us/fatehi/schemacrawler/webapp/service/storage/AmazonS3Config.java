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
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// @Configuration
public class AmazonS3Config
{

  @Value("${aws.access.key.id}")
  private String awsKeyId;

  @Value("${aws.access.key.secret}")
  private String awsKeySecret;

  @Value("${aws.region}")
  private String awsRegion;

  @Value("${aws.s3.bucket}")
  private String awsS3Bucket;

  @Bean(name = "awsRegion")
  public Region getAWSRegion()
  {
    if (StringUtils.isBlank(awsRegion))
    {
      throw new RuntimeException("No AWS region provided");
    }
    return Region.getRegion(Regions.fromName(awsRegion));
  }

  @Bean(name = "awsCredentialsProvider")
  public AWSCredentialsProvider getAWSCredentials()
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
  public String getAWSS3Bucket()
  {
    return awsS3Bucket;
  }

}
