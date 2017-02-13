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


import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Paths;
import java.util.Optional;

import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import us.fatehi.schemacrawler.webapp.schemacrawler.SchemaCrawlerService;
import us.fatehi.schemacrawler.webapp.storage.StorageService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SchemaCrawlerControllerTest
{

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private MockMvc mvc;
  @MockBean
  private StorageService storageService;
  @MockBean
  private SchemaCrawlerService scService;

  @Test
  public void formWithUpload()
    throws Exception
  {
    final MockMultipartFile multipartFile = new MockMultipartFile("file",
                                                                  "test.db",
                                                                  "application/octet-stream",
                                                                  RandomUtils
                                                                    .nextBytes(5));

    when(storageService.resolve(any(), eq("db")))
      .thenReturn(Optional.ofNullable(Paths.get("/")));
    when(scService.createSchemaCrawlerDiagram(any(), eq("png")))
      .thenReturn(Paths.get("/"));

    mvc
      .perform(fileUpload("/schemacrawler").file(multipartFile)
        .param("name", "Sualeh").param("email", "sualeh@hotmail.com"))
      .andExpect(status().is2xxSuccessful());

    then(storageService).should().store(eq(multipartFile), any(), eq("db"));
  }

  @Test
  public void index()
    throws Exception
  {
    mockMvc.perform(get("/schemacrawler"))
      .andExpect(content().string(containsString("SchemaCrawler Diagram")));
  }
  
}
