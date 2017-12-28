/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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
package us.fatehi.schemacrawler.webapp;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.io.FileReader;
import java.nio.file.Path;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import us.fatehi.schemacrawler.webapp.model.SchemaCrawlerDiagramRequest;
import us.fatehi.schemacrawler.webapp.service.storage.FileExtensionType;
import us.fatehi.schemacrawler.webapp.service.storage.StorageService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SchemaCrawlerControllerHappyPathTest
{

  @Autowired
  private MockMvc mvc;
  @Autowired
  private StorageService storageService;

  @Test
  public void happyPath()
    throws Exception
  {
    final InputStreamSource testDbStreamSource = new ClassPathResource("/test.db");
    final MockMultipartFile multipartFile = new MockMultipartFile("file",
                                                                  "test.db",
                                                                  "application/octet-stream",
                                                                  testDbStreamSource
                                                                    .getInputStream());

    final MvcResult result1 = mvc
      .perform(fileUpload("/schemacrawler").file(multipartFile)
        .param("name", "Sualeh").param("email", "sualeh@hotmail.com"))
      .andExpect(view().name("SchemaCrawlerDiagramResult"))
      .andExpect(status().is2xxSuccessful()).andReturn();

    final SchemaCrawlerDiagramRequest diagramRequest = (SchemaCrawlerDiagramRequest) result1
      .getModelAndView().getModel().get("diagramRequest");
    final String key = diagramRequest.getKey();

    final Optional<Path> sqlitePathOptional = storageService
      .resolve(key, FileExtensionType.SQLITE_DB);
    assertThat(sqlitePathOptional.isPresent(), is(equalTo(true)));
    final Optional<Path> pngPathOptional = storageService
      .resolve(key, FileExtensionType.PNG);
    assertThat(pngPathOptional.isPresent(), is(equalTo(true)));
    final Optional<Path> jsonPathOptional = storageService
      .resolve(key, FileExtensionType.JSON);
    assertThat(jsonPathOptional.isPresent(), is(equalTo(true)));

    final SchemaCrawlerDiagramRequest schemaCrawlerDiagramRequestFromJson = SchemaCrawlerDiagramRequest
      .fromJson(IOUtils
        .toString(new FileReader(jsonPathOptional.get().toFile())));
    assertThat(diagramRequest,
               is(equalTo(schemaCrawlerDiagramRequestFromJson)));

    final MvcResult result2 = mvc.perform(get("/schemacrawler/" + key))
      .andExpect(view().name("SchemaCrawlerDiagram"))
      .andExpect(status().is2xxSuccessful()).andReturn();
    assertThat(result2.getResponse().getContentAsString(),
               containsString("/schemacrawler/images/" + key));

    final MvcResult result3 = mvc
      .perform(get("/schemacrawler/images/" + key)
        .accept(MediaType.IMAGE_PNG))
      .andExpect(status().isOk()).andReturn();
    final int contentLength = result3.getResponse().getContentLength();
    assertThat(contentLength, is(greaterThan(0)));
    // assertThat(contentLength, is(greaterThan(14_500)));
    // assertThat(contentLength, is(lessThan(15_500)));

  }

}
