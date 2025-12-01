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
package us.fatehi.schemacrawler.webapp.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static us.fatehi.schemacrawler.webapp.service.storage.FileExtensionType.SQLITE_DB;
import static us.fatehi.schemacrawler.webapp.test.utility.TestUtility.mockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import us.fatehi.schemacrawler.webapp.model.DiagramRequest;
import us.fatehi.schemacrawler.webapp.service.storage.StorageService;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("local")
public class RequestControllerTest {

  @Autowired private MockMvc mvc;
  @Autowired private ThreadPoolTaskExecutor pool;
  @Autowired private StorageService storageService;

  @Test
  public void formWithNoParameters() throws Exception {

    mvc.perform(multipart("/schemacrawler").file(mockMultipartFile()))
        .andExpect(model().errorCount(2))
        .andExpect(model().attributeHasFieldErrors("diagramRequest", "name", "email"))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  public void formWithUpload() throws Exception {

    final MvcResult mvcResult =
        mvc.perform(
                multipart("/schemacrawler")
                    .file(mockMultipartFile())
                    .param("name", "Sualeh")
                    .param("email", "sualeh@hotmail.com"))
            .andExpect(view().name("SchemaCrawlerDiagramResult"))
            .andExpect(status().is2xxSuccessful())
            .andReturn();

    final DiagramRequest diagramRequest =
        (DiagramRequest) mvcResult.getModelAndView().getModel().get("diagramRequest");
    /* final boolean awaitTermination = */ pool.getThreadPoolExecutor()
        .awaitTermination(3, TimeUnit.SECONDS);

    final Optional<Path> localDatabaseFile =
        storageService.retrieveLocal(diagramRequest.getKey(), SQLITE_DB);
    assertThat(localDatabaseFile.isPresent(), is(true));
    assertThat(Files.size(localDatabaseFile.get()), is(9216L));
  }

  @Test
  public void formWithUploadNotADatabase() throws Exception {

    final MockMultipartFile multipartFile =
        new MockMultipartFile(
            "file", "test.db", "application/octet-stream", RandomUtils.secure().randomBytes(5));

    final MvcResult mvcResult =
        mvc.perform(
                multipart("/schemacrawler")
                    .file(multipartFile)
                    .param("name", "Sualeh")
                    .param("email", "sualeh@hotmail.com"))
            .andExpect(view().name("redirect:/error"))
            .andExpect(status().is3xxRedirection())
            .andReturn();

    final Exception resolvedException = mvcResult.getResolvedException();
    assertThat(
        resolvedException.getMessage(),
        matchesPattern(
            Pattern.compile(
                ".*Expected a SQLite database file, but got a file of type.*", Pattern.DOTALL)));
  }

  @Test
  public void index() throws Exception {
    mvc.perform(get("/schemacrawler"))
        .andExpect(content().string(containsString("SchemaCrawler Diagram")));
  }
}
