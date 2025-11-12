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

import static com.atlassian.oai.validator.mockmvc.OpenApiValidationMatchers.openApi;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.IOUtils.toInputStream;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static us.fatehi.schemacrawler.webapp.controller.URIConstants.API_PREFIX;
import static us.fatehi.schemacrawler.webapp.service.storage.FileExtensionType.JSON;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import us.fatehi.schemacrawler.webapp.model.DiagramKey;
import us.fatehi.schemacrawler.webapp.model.DiagramRequest;
import us.fatehi.schemacrawler.webapp.service.storage.StorageService;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("local")
public class ResultControllerAPITest {

  @Autowired private StorageService storageService;
  @Autowired private MockMvc mvc;

  @Test
  public void getBadKey() throws Exception {

    final String key = "badkey";
    final String resultsUrlPath = API_PREFIX + "/" + key;

    final MvcResult result =
        mvc.perform(
                get(resultsUrlPath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn();

    assertThat(result, is(notNullValue()));

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

    final String resultsUrlPath = API_PREFIX + "/" + key;

    final MvcResult result =
        mvc.perform(
                get(resultsUrlPath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(openApi().isValid("api/schemacrawler-web-application.yaml"))
            .andReturn();

    assertThat(result, is(notNullValue()));
    final String returnJson = result.getResponse().getContentAsString();

    assertThat(returnJson, containsString("Bad error"));
  }

  @Test
  public void getMissingKey() throws Exception {

    final String key = "missingkey01";
    final String resultsUrlPath = API_PREFIX + "/" + key;

    final MvcResult result =
        mvc.perform(
                get(resultsUrlPath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(openApi().isValid("api/schemacrawler-web-application.yaml"))
            .andReturn();

    assertThat(result, is(notNullValue()));
  }

  @Test
  public void getResults() throws Exception {

    final DiagramRequest diagramRequest = new DiagramRequest();
    diagramRequest.setName("Sualeh Fatehi");
    diagramRequest.setEmail("sualeh@hotmail.com");

    final DiagramKey key = diagramRequest.getKey();

    storageService.store(() -> toInputStream(diagramRequest.toJson(), UTF_8), key, JSON);

    final String resultsUrlPath = API_PREFIX + "/" + key;

    final MvcResult result =
        mvc.perform(
                get(resultsUrlPath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(openApi().isValid("api/schemacrawler-web-application.yaml"))
            .andReturn();

    assertThat(result, is(notNullValue()));
    final String returnJson = result.getResponse().getContentAsString();

    final ObjectMapper objectMapper = new ObjectMapper();
    final JsonNode jsonNode = objectMapper.readTree(returnJson);

    final String keyNode = jsonNode.get("key").toString();
    final DiagramKey resultKey = objectMapper.readValue(keyNode, DiagramKey.class);

    assertThat(resultKey, is(key));
    assertThat(jsonNode.get("name").asText(), is("Sualeh Fatehi"));
  }
}
