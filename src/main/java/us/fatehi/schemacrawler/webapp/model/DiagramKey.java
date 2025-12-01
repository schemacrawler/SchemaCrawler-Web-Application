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

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;

public class DiagramKey implements Serializable {

  @Serial private static final long serialVersionUID = 3453873731406876293L;

  @Pattern(regexp = "[a-z0-9]{12}")
  @Size(min = 12, max = 12, message = "Invalid key length")
  private final String key;

  public DiagramKey() {
    key = RandomStringUtils.secure().nextAlphanumeric(12).toLowerCase();
  }

  public DiagramKey(final String key) {
    this.key = validateKey(key);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final DiagramKey other = (DiagramKey) obj;
    if (!Objects.equals(key, other.key)) {
      return false;
    }
    return true;
  }

  public String getKey() {
    return key;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (key == null ? 0 : key.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return key;
  }

  /**
   * Prevent malicious injection attacks.
   *
   * @param key Key
   * @throws Exception On a badly constructed key.
   */
  private String validateKey(final String key) throws RuntimeException {
    if (StringUtils.length(key) != 12 || !StringUtils.isAlphanumeric(key)) {
      throw new InternalRuntimeException("Invalid key <%s>".formatted(key));
    }
    return key.toLowerCase();
  }
}
