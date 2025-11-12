/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static org.apache.commons.io.IOUtils.toInputStream;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static us.fatehi.schemacrawler.webapp.service.storage.FileExtensionType.JSON;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import us.fatehi.schemacrawler.webapp.model.DiagramKey;
import us.fatehi.schemacrawler.webapp.model.DiagramRequest;
import us.fatehi.schemacrawler.webapp.service.storage.StorageService;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("local")
public class ResultControllerTest {

  @Autowired private StorageService storageService;
  @Autowired private MockMvc mvc;

  @Test
  public void getBadKey() throws Exception {

    final String key = "badkey";
    final String resultsUrlPath = "/schemacrawler/results/" + key;

    final MvcResult result =
        mvc.perform(get(resultsUrlPath)).andExpect(status().is4xxClientError()).andReturn();

    final Throwable exception = ExceptionUtils.getRootCause(result.getResolvedException());
    assertThat(exception, is(instanceOf(InternalRuntimeException.class)));
    assertThat(exception.getMessage(), is("Invalid key <badkey>"));
  }

  @Test
  public void getErrorResults() throws Exception {

    final DiagramRequest diagramRequest = new DiagramRequest();
    diagramRequest.setName("Sualeh Fatehi");
    diagramRequest.setEmail("sualeh@hotmail.com");
    diagramRequest.setError("Bad error");

    final DiagramKey key = diagramRequest.getKey();

    storageService.store(() -> toInputStream(diagramRequest.toJson(), UTF_8), key, JSON);

    final String resultsUrlPath = "/schemacrawler/results/" + key;

    final MvcResult result =
        mvc.perform(get(resultsUrlPath))
            .andExpect(view().name("redirect:/error"))
            .andExpect(status().is3xxRedirection())
            .andReturn();

    final Exception exception = result.getResolvedException();
    assertThat(exception.getMessage(), containsString("Bad error"));
  }

  @Test
  public void getMissingKey() throws Exception {

    final String key = "missingkey01";
    final String resultsUrlPath = "/schemacrawler/results/" + key;

    final MvcResult result =
        mvc.perform(get(resultsUrlPath))
            .andExpect(view().name("redirect:/error"))
            .andExpect(status().is3xxRedirection())
            .andReturn();

    final Throwable exception = ExceptionUtils.getRootCause(result.getResolvedException());
    assertThat(exception, is(instanceOf(ExecutionRuntimeException.class)));
    assertThat(exception.getMessage(), is("Cannot find request for <missingkey01>"));
  }

  @Test
  public void getResults() throws Exception {

    final DiagramRequest diagramRequest = new DiagramRequest();
    diagramRequest.setName("Sualeh Fatehi");
    diagramRequest.setEmail("sualeh@hotmail.com");

    final DiagramKey key = diagramRequest.getKey();

    storageService.store(() -> toInputStream(diagramRequest.toJson(), UTF_8), key, JSON);

    final String resultsUrlPath = "/schemacrawler/results/" + key;
    final String diagramUrlPath = resultsUrlPath + "/diagram";

    final MvcResult result =
        mvc.perform(get(resultsUrlPath))
            .andExpect(view().name("SchemaCrawlerDiagram"))
            .andExpect(status().is2xxSuccessful())
            .andReturn();
    assertThat(result.getResponse().getContentAsString(), containsString(diagramUrlPath));
  }
}
