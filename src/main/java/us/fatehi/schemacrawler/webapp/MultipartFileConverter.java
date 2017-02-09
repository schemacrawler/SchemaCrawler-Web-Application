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


import org.springframework.core.convert.converter.Converter;
import org.springframework.web.multipart.MultipartFile;

/**
 * Converts a multi-part file to a string, which is the name of the
 * uploaded file. This is needed for input form validation, and also for
 * saving details of the request.
 *
 * @author Sualeh Fatehi
 */
public class MultipartFileConverter
  implements Converter<MultipartFile, String>
{

  /**
   * Converts a multi-part file to a string, which is the name of the
   * uploaded file.
   */
  @Override
  public String convert(final MultipartFile file)
  {
    if (file == null)
    {
      return null;
    }
    else
    {
      return file.getOriginalFilename();
    }
  }

}
