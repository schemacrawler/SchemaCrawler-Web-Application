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
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:/aws-s3-storage.properties")
public class AmazonS3PropertiesConfig
  implements AmazonS3Config
{

  @Value("${aws.access.key.id}")
  @NotNull
  private String awsKeyId;

  @Value("${aws.access.key.secret}")
  @NotNull
  private String awsKeySecret;

  @Value("${aws.region}")
  @NotNull
  private String awsRegion;

  @Value("${aws.s3.bucket}")
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
