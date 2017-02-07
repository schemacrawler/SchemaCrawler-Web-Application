package us.fatehi.schemacrawler.webapp.repository;


import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Component;

import us.fatehi.schemacrawler.webapp.model.SchemaCrawlerSQLiteDiagramRequest;

@Component
@RepositoryRestResource(collectionResourceRel = "requests", path = "requests")
public interface SchemaCrawlerSQLiteDiagramRequestRepository
  extends CrudRepository<SchemaCrawlerSQLiteDiagramRequest, Long>,
  PagingAndSortingRepository<SchemaCrawlerSQLiteDiagramRequest, Long>
{

  List<SchemaCrawlerSQLiteDiagramRequest> findByEmail(@Param("email") String email);

  SchemaCrawlerSQLiteDiagramRequest findByKey(@Param("key") String key);

}
