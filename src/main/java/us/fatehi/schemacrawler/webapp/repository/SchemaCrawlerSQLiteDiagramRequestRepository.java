package us.fatehi.schemacrawler.webapp.repository;


import java.util.List;
import org.springframework.data.repository.CrudRepository;
import us.fatehi.schemacrawler.webapp.model.SchemaCrawlerSQLiteDiagramRequest;

public interface SchemaCrawlerSQLiteDiagramRequestRepository
  extends CrudRepository<SchemaCrawlerSQLiteDiagramRequest, Long>
{

  List<SchemaCrawlerSQLiteDiagramRequest> findByEmail(String email);

  List<SchemaCrawlerSQLiteDiagramRequest> findByKey(String key);

}
