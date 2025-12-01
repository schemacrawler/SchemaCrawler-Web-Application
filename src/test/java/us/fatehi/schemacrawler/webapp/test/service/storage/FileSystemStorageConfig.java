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
package us.fatehi.schemacrawler.webapp.test.service.storage;

import java.nio.file.Path;

import jakarta.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
public class FileSystemStorageConfig {

  @Value("${SC_WEBAPP_STORAGE:./target/schemacrawler-web-app-1}")
  @NotNull(message = "SC_WEBAPP_STORAGE not provided")
  private Path fileSystemStorageRootPath;

  @Bean("fileSystemStorageRootPath")
  public Path fileSystemStorageRootPath() {
    return fileSystemStorageRootPath.normalize().toAbsolutePath();
  }
}
