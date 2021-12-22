/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static us.fatehi.schemacrawler.webapp.controller.URIConstants.API_PREFIX;
import static us.fatehi.schemacrawler.webapp.service.storage.FileExtensionType.SQLITE_DB;
import static us.fatehi.schemacrawler.webapp.test.utility.TestUtility.mockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.fatehi.schemacrawler.webapp.model.DiagramKey;
import us.fatehi.schemacrawler.webapp.service.storage.StorageService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
public class RequestControllerAPITest {

  @Autowired private MockMvc mvc;
  @Autowired private ThreadPoolTaskExecutor pool;
  @Autowired private StorageService storageService;

  @Test
  public void apiWithNoParameters() throws Exception {

    final MvcResult result =
        mvc.perform(
                multipart(API_PREFIX)
                    .file(mockMultipartFile())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn();

    assertThat(result, is(notNullValue()));
    final String returnJson = result.getResponse().getContentAsString();

    final ObjectMapper objectMapper = new ObjectMapper();
    final JsonNode jsonNode = objectMapper.readTree(returnJson);

    assertThat(
        jsonNode.get("error").asText(), is("[email: Email is required, name: Name is required]"));
  }

  @Test
  public void apiWithUpload() throws Exception {

    final MvcResult result =
        mvc.perform(
                multipart(API_PREFIX)
                    .file(mockMultipartFile())
                    .param("name", "Sualeh")
                    .param("email", "sualeh@hotmail.com")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn();

    assertThat(result, is(notNullValue()));
    final String returnJson = result.getResponse().getContentAsString();

    final ObjectMapper objectMapper = new ObjectMapper();
    final JsonNode jsonNode = objectMapper.readTree(returnJson);

    assertThat(jsonNode.get("error"), is(nullValue()));
    assertThat(jsonNode.get("title"), is(nullValue()));

    final String keyNode = jsonNode.get("key").toString();
    final DiagramKey key = objectMapper.readValue(keyNode, DiagramKey.class);

    final String locationHeaderValue = result.getResponse().getHeaderValue("Location").toString();
    assertThat(locationHeaderValue, endsWith(key.getKey()));

    pool.getThreadPoolExecutor().awaitTermination(3, TimeUnit.SECONDS);

    final Optional<Path> localDatabaseFile = storageService.retrieveLocal(key, SQLITE_DB);
    assertThat(localDatabaseFile.isPresent(), is(true));
    assertThat(Files.size(localDatabaseFile.get()), is(9216L));
  }

  @Test
  public void apiWithUploadNotADatabase() throws Exception {

    final MockMultipartFile multipartFile =
        new MockMultipartFile(
            "file", "test.db", "application/octet-stream", RandomUtils.nextBytes(5));

    final MvcResult result =
        mvc.perform(
                multipart(API_PREFIX)
                    .file(multipartFile)
                    .param("name", "Sualeh")
                    .param("email", "sualeh@hotmail.com")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn();

    assertThat(result, is(notNullValue()));
    final String returnJson = result.getResponse().getContentAsString();
    System.err.println(returnJson);

    final ObjectMapper objectMapper = new ObjectMapper();
    final JsonNode jsonNode = objectMapper.readTree(returnJson);

    assertThat(
        jsonNode.get("error").asText(),
        startsWith("Expected a SQLite database file, but got a file of type "));
  }
}
