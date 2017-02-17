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
package us.fatehi.schemacrawler.webapp.service.schemacrawler;


import java.nio.file.Path;

/**
 * Service for SchemaCrawler functions.
 *
 * @author Sualeh Fatehi
 */
public interface SchemaCrawlerService
{

  /**
   * Reads in a database file, and generates a database diagram, in the
   * file format specified by the extension. All files are on the local
   * default file-system.
   *
   * @param dbFile
   *        Path to a database file.
   * @param extension
   *        Filename extension for a diagram image. Should be a file
   *        type supported by GraphViz.
   * @return Path to a diagram file.
   * @throws Exception
   *         Any exceptions thrown in the process of generating a
   *         diagram.
   */
  Path createSchemaCrawlerDiagram(Path dbFile, String extension)
    throws Exception;

}
