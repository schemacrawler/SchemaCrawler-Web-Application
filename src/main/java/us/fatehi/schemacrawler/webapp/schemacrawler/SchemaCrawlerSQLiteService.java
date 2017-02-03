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
