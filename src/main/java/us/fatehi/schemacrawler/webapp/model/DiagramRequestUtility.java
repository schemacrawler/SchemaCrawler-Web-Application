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
package us.fatehi.schemacrawler.webapp.model;

import static tools.jackson.core.StreamReadFeature.IGNORE_UNDEFINED;
import static tools.jackson.core.StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION;
import static tools.jackson.core.StreamWriteFeature.IGNORE_UNKNOWN;
import static tools.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;
import static tools.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static tools.jackson.databind.SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS;
import static tools.jackson.databind.SerializationFeature.USE_EQUALITY_FOR_OBJECT_ID;

import java.io.Reader;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import static java.util.Objects.requireNonNull;

import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class DiagramRequestUtility {

  private static ObjectMapper newConfiguredObjectMapper() {

    final MapperBuilder<? extends ObjectMapper, ?> mapperBuilder = JsonMapper.builder();

    requireNonNull(mapperBuilder, "No mapper builder provided");
    mapperBuilder.enable(ORDER_MAP_ENTRIES_BY_KEYS, INDENT_OUTPUT, USE_EQUALITY_FOR_OBJECT_ID);
    mapperBuilder.disable(FAIL_ON_NULL_FOR_PRIMITIVES);
    mapperBuilder.enable(INCLUDE_SOURCE_IN_LOCATION, IGNORE_UNDEFINED);
    mapperBuilder.enable(IGNORE_UNKNOWN);

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

    mapperBuilder.addMixIn(DiagramRequest.class, JacksonAnnotationMixIn.class);

    final ObjectMapper objectMapper = mapperBuilder.build();
    return objectMapper;
  }

  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    final ObjectMapper mapper = newConfiguredObjectMapper();
    return mapper;
  }

  String diagramRequestToJson(final DiagramRequest diagramRequest) {
    requireNonNull(diagramRequest, "No diagram request provided");
    try {
      return objectMapper().writeValueAsString(diagramRequest);
    } catch (final JacksonException e) {
      throw new ExecutionRuntimeException("Cannot serialize diagram request", e);
    }
  }

  DiagramRequest readDiagramRequest(final Reader diagramRequestReader) {
    requireNonNull(diagramRequestReader, "No diagram request reader provided");
    try {
      return objectMapper().readValue(diagramRequestReader, DiagramRequest.class);
    } catch (final JacksonException e) {
      throw new ExecutionRuntimeException(
          "Cannot deserialize diagram request%n%s".formatted(diagramRequestReader), e);
    }
  }
}
