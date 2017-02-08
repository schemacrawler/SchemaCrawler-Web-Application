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
package us.fatehi.schemacrawler.webapp.schemacrawler;


import static schemacrawler.tools.integration.graph.GraphOutputFormat.png;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;

import org.springframework.stereotype.Service;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.ConnectionOptions;
import schemacrawler.schemacrawler.ExcludeAll;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.integration.graph.GraphExecutable;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.sqlite.SQLiteDatabaseConnector;

@Service
public class SchemaCrawlerSQLiteService
  implements SchemaCrawlerService
{

  @Override
  public Connection createDatabaseConnection(final Path file)
    throws Exception
  {
    final Config config = new Config();
    config.put("server", "sqlite");
    config.put("database", file.toString());
    final ConnectionOptions connectionOptions = new SQLiteDatabaseConnector()
      .newDatabaseConnectionOptions(config);
    return connectionOptions.getConnection();
  }

  @Override
  public Path createSchemaCrawlerDiagram(final Connection connection)
    throws Exception
  {
    final SchemaCrawlerOptions options = new SchemaCrawlerOptions();
    options.setSchemaInfoLevel(SchemaInfoLevelBuilder.standard());
    options.setRoutineInclusionRule(new ExcludeAll());

    final Path outputFile = Files.createTempFile("schemacrawler", ".png");
    final OutputOptions outputOptions = new OutputOptions(png, outputFile);

    final Executable executable = new GraphExecutable("schema");
    executable.setSchemaCrawlerOptions(options);
    executable.setOutputOptions(outputOptions);
    executable.execute(connection);

    return outputFile;
  }

}
