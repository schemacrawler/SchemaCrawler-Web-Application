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
package us.fatehi.schemacrawler.webapp.service.storage;

public enum FileExtensionType {
  SQLITE_DB("db", "application/x-sqlite3"),
  PNG("png", "image/png"),
  JSON("json", "application/json"),
  LOG("log", "text/plain"),
  DATA("data", "application/octet-stream");

  private final String extension;
  private final String mimeType;

  FileExtensionType(final String extension, final String mimeType) {
    this.extension = extension;
    this.mimeType = mimeType;
  }

  public String getExtension() {
    return extension;
  }

  public String getMimeType() {
    return mimeType;
  }

  @Override
  public String toString() {
    return getExtension();
  }
}
