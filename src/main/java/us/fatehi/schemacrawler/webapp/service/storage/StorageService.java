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
package us.fatehi.schemacrawler.webapp.service.storage;


import java.nio.file.Path;
import java.util.Optional;

import org.springframework.core.io.InputStreamSource;

/**
 * Service to store files.
 *
 * @author Sualeh Fatehi
 */
public interface StorageService
{

  /**
   * Initializes the service.
   *
   * @throws Exception
   *         On an exception.
   */
  void init()
    throws Exception;

  /**
   * Resolves a filename key and extension into a local default
   * file-system path to a file.
   *
   * @param filenameKey
   *        Filename key.
   * @param extension
   *        Filename extension.
   * @return a local file-system path to a file, if one is found.
   * @throws Exception
   *         Exception resolving a path.
   */
  Optional<Path> resolve(String filenameKey, FileExtensionType extension)
    throws Exception;

  /**
   * Stores a stream given a filename key and extension.
   *
   * @param Stream
   *        Input stream
   * @param filenameKey
   *        Filename key.
   * @param extension
   *        Filename extension.
   * @throws Exception
   *         Exception storing a file.
   */
  void store(InputStreamSource stream,
             String filenameKey,
             FileExtensionType extension)
    throws Exception;

}
