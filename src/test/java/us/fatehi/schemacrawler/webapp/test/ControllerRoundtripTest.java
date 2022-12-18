/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.newBufferedReader;
import static java.time.Duration.ofMillis;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static us.fatehi.schemacrawler.webapp.service.storage.FileExtensionType.JSON;
import static us.fatehi.schemacrawler.webapp.service.storage.FileExtensionType.PNG;
import static us.fatehi.schemacrawler.webapp.service.storage.FileExtensionType.SQLITE_DB;
import static us.fatehi.schemacrawler.webapp.test.utility.TestUtility.mockMultipartFile;

import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import us.fatehi.schemacrawler.webapp.model.DiagramKey;
import us.fatehi.schemacrawler.webapp.model.DiagramRequest;
import us.fatehi.schemacrawler.webapp.service.storage.StorageService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
public class ControllerRoundtripTest {

  @Autowired private MockMvc mvc;

  @Autowired private StorageService storageService;

  @Test
  public void happyPath() throws Exception {

    final MvcResult result =
        mvc.perform(
                multipart("/schemacrawler")
                    .file(mockMultipartFile())
                    .param("name", "Sualeh")
                    .param("email", "sualeh@hotmail.com"))
            .andExpect(view().name("SchemaCrawlerDiagramResult"))
            .andExpect(status().is2xxSuccessful())
            .andReturn();

    final DiagramRequest diagramRequest =
        (DiagramRequest) result.getModelAndView().getModel().get("diagramRequest");
    final DiagramKey key = diagramRequest.getKey();

    // Wait for diagram to be created
    assertTimeoutPreemptively(
        ofMillis(5000),
        () -> {
          while (!storageService.retrieveLocal(key, PNG).isPresent()) {
            Thread.sleep(200);
          }
        });

    final Optional<Path> sqlitePathOptional = storageService.retrieveLocal(key, SQLITE_DB);
    assertThat(sqlitePathOptional.isPresent(), is(equalTo(true)));
    final Optional<Path> jsonPathOptional = storageService.retrieveLocal(key, JSON);
    assertThat(jsonPathOptional.isPresent(), is(equalTo(true)));

    final DiagramRequest diagramRequestFromJson =
        DiagramRequest.fromJson(newBufferedReader(jsonPathOptional.get(), UTF_8));
    assertThat(diagramRequest, is(equalTo(diagramRequestFromJson)));

    final String diagramUrlPath = "/schemacrawler/results/" + key + "/diagram";

    final MvcResult result2 =
        mvc.perform(get("/schemacrawler/results/" + key))
            .andExpect(view().name("SchemaCrawlerDiagram"))
            .andExpect(status().is2xxSuccessful())
            .andReturn();
    assertThat(result2.getResponse().getContentAsString(), containsString(diagramUrlPath));

    final MvcResult result3 =
        mvc.perform(get(diagramUrlPath).accept(MediaType.IMAGE_PNG))
            .andExpect(status().isOk())
            .andReturn();
    final int contentLength = result3.getResponse().getContentLength();
    assertThat(contentLength, is(greaterThan(0)));
  }
}
