package us.fatehi.schemacrawler.webapp.service.storage;


import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Region;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public interface AmazonS3Config
{

  @Bean(name = "awsRegion")
  Region awsRegion();

  @Bean(name = "awsCredentials")
  AWSCredentialsProvider awsCredentials();

  @Bean(name = "awsS3Bucket")
  String awsS3Bucket();

}
