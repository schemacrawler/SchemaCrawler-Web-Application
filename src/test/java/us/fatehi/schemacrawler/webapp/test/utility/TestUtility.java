package us.fatehi.schemacrawler.webapp.test.utility;

import java.io.IOException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mock.web.MockMultipartFile;

public class TestUtility {

  private TestUtility() {
    // Prevent instantiation
  }

public static MockMultipartFile mockMultipartFile() throws IOException {
    final InputStreamSource testDbStreamSource = new ClassPathResource("/test.db");
    final MockMultipartFile multipartFile =
        new MockMultipartFile(
            "file", "test.db", "application/octet-stream", testDbStreamSource.getInputStream());
    return multipartFile;
  }
}
