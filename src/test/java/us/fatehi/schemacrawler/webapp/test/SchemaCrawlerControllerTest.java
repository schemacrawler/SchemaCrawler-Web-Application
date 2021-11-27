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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static us.fatehi.schemacrawler.webapp.service.storage.FileExtensionType.SQLITE_DB;

import java.nio.file.Paths;
import java.util.Optional;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import us.fatehi.schemacrawler.webapp.service.processing.ProcessingService;
import us.fatehi.schemacrawler.webapp.service.schemacrawler.SchemaCrawlerService;
import us.fatehi.schemacrawler.webapp.service.storage.StorageService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
public class SchemaCrawlerControllerTest {

  @Autowired private MockMvc mvc;
  @MockBean private StorageService storageService;
  @MockBean private SchemaCrawlerService scService;
  @SpyBean private ProcessingService processingService;

  private final ArgumentMatcher<MultipartFile> matcher =
      (final MultipartFile argFile) -> {
        assertThat(
            "Content type should match",
            argFile.getContentType(),
            is(equalTo("application/octet-stream")));
        assertThat(
            "Original filename should match",
            argFile.getOriginalFilename(),
            is(equalTo("test.db")));
        assertThat("File should match", argFile.getName(), is(equalTo("file")));
        return true;
      };

  private final MockMultipartFile multipartFile =
      new MockMultipartFile(
          "file", "test.db", "application/octet-stream", RandomUtils.nextBytes(5));

  @Test
  public void formWithNoParameters() throws Exception {

    mvc.perform(multipart("/schemacrawler").file(multipartFile))
        .andExpect(model().errorCount(2))
        .andExpect(model().attributeHasFieldErrors("diagramRequest", "name", "email"))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  public void formWithUpload() throws Exception {

    when(storageService.retrieveLocal(any(), eq(SQLITE_DB)))
        .thenReturn(Optional.ofNullable(Paths.get("/")));
    when(scService.createSchemaCrawlerDiagram(any(), anyString(), eq("png")))
        .thenReturn(Paths.get("/"));

    mvc.perform(
            multipart("/schemacrawler")
                .file(multipartFile)
                .param("name", "Sualeh")
                .param("email", "sualeh@hotmail.com"))
        .andExpect(view().name("SchemaCrawlerDiagramResult"))
        .andExpect(status().is2xxSuccessful());

    then(storageService).should().storeLocal(argThat(matcher), any(), eq(SQLITE_DB));

    // NOTE: The image file is not created - assert that by testing the service itself
  }

  @Test
  public void index() throws Exception {
    mvc.perform(get("/schemacrawler"))
        .andExpect(content().string(containsString("SchemaCrawler Diagram")));
  }
}
