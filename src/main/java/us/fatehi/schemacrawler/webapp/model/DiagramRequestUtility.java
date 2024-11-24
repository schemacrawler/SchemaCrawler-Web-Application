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
package us.fatehi.schemacrawler.webapp.model;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static com.fasterxml.jackson.databind.SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS;
import static com.fasterxml.jackson.databind.SerializationFeature.USE_EQUALITY_FOR_OBJECT_ID;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_ENUMS_USING_TO_STRING;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.Reader;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;

@Configuration
public class DiagramRequestUtility {

  @Bean
  @Primary
  public ObjectMapper objectMapper() {

    @JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
      "key",
      "timestamp",
      "name",
      "email",
      "file",
      "title",
      "error",
    })
    abstract class JacksonAnnotationMixIn {
      @JsonUnwrapped public DiagramKey key;
    }

    final ObjectMapper mapper = new ObjectMapper();
    mapper.enable(
        ORDER_MAP_ENTRIES_BY_KEYS,
        INDENT_OUTPUT,
        USE_EQUALITY_FOR_OBJECT_ID,
        WRITE_ENUMS_USING_TO_STRING);
    mapper.setSerializationInclusion(NON_NULL);
    mapper.addMixIn(DiagramRequest.class, JacksonAnnotationMixIn.class);
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    mapper.registerModule(new JavaTimeModule());
    return mapper;
  }

  String diagramRequestToJson(final DiagramRequest diagramRequest) {
    requireNonNull(diagramRequest, "No diagram request provided");
    try {
      return objectMapper().writeValueAsString(diagramRequest);
    } catch (final JsonProcessingException e) {
      throw new ExecutionRuntimeException("Cannot serialize diagram request", e);
    }
  }

  DiagramRequest readDiagramRequest(final Reader diagramRequestReader) {
    requireNonNull(diagramRequestReader, "No diagram request reader provided");
    try {
      return objectMapper().readValue(diagramRequestReader, DiagramRequest.class);
    } catch (final IOException e) {
      throw new ExecutionRuntimeException(
          String.format("Cannot deserialize diagram request%n%s", diagramRequestReader), e);
    }
  }
}
