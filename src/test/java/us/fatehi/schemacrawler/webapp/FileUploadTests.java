package us.fatehi.schemacrawler.webapp;


import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import us.fatehi.schemacrawler.webapp.storage.StorageService;

@Ignore
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class FileUploadTests
{

  @Autowired
  private MockMvc mvc;

  @MockBean
  private StorageService storageService;

  @Test
  public void shouldSaveUploadedFile()
    throws Exception
  {
    MockMultipartFile multipartFile = new MockMultipartFile("file",
                                                            "test.txt",
                                                            "text/plain",
                                                            "Spring Framework"
                                                              .getBytes());
    this.mvc.perform(fileUpload("/").file(multipartFile))
      .andExpect(status().isFound())
      .andExpect(header().string("Location", "/"));

    then(this.storageService).should().store(multipartFile, "abcd");
  }

}
