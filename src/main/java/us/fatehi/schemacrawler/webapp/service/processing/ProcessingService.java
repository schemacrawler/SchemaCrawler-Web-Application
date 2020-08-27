/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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
package us.fatehi.schemacrawler.webapp.service.processing;


import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.nio.file.Path;

import us.fatehi.schemacrawler.webapp.model.SchemaCrawlerDiagramRequest;

/**
 * Service for processing.
 *
 * @author Sualeh Fatehi
 */
public interface ProcessingService
{

  /**
   * Generate SchemaCrawler diagram, and store it in storage
   *
   * @param diagramRequest
   * @param localPath
   * @throws Exception On an exception
   */
  void generateSchemaCrawlerDiagram(final SchemaCrawlerDiagramRequest diagramRequest,
                                    final Path localPath)
    throws Exception;

}
