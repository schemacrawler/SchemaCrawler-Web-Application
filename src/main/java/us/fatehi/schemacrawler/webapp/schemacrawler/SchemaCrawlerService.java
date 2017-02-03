package us.fatehi.schemacrawler.webapp.schemacrawler;


import java.nio.file.Path;
import java.sql.Connection;

public interface SchemaCrawlerService
{

  Connection createDatabaseConnection(Path file)
    throws Exception;

  Path createSchemaCrawlerDiagram(Connection connection)
    throws Exception;

}
