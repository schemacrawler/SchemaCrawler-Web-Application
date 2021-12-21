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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;

import java.io.StringReader;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import us.fatehi.schemacrawler.webapp.model.DiagramRequest;

public class DiagramRequestUtilityTest {

  @Test
  public void equals() {
    EqualsVerifier.simple().forClass(DiagramRequest.class).verify();
  }

  @Test
  public void roundtrip() throws Exception {

    final DiagramRequest diagramRequestSource = new DiagramRequest();
    diagramRequestSource.setName("Sualeh Fatehi");
    diagramRequestSource.setEmail("sualeh@hotmail.com");

    final String diagramRequestJson = diagramRequestSource.toJson();
    System.out.println(diagramRequestJson);

    final DiagramRequest diagramRequestMarshalled =
        DiagramRequest.fromJson(new StringReader(diagramRequestJson));

    assertThat(
        diagramRequestMarshalled.getKey().getKey(), is(diagramRequestSource.getKey().getKey()));
    assertThat(diagramRequestMarshalled.getTimestamp(), is(diagramRequestSource.getTimestamp()));
    assertThat(diagramRequestMarshalled.getName(), is(diagramRequestSource.getName()));
    assertThat(diagramRequestMarshalled.getEmail(), is(diagramRequestSource.getEmail()));
    assertThat(diagramRequestMarshalled.getFile(), is(nullValue()));
    assertThat(diagramRequestMarshalled.getTitle(), is(nullValue()));
  }
}
