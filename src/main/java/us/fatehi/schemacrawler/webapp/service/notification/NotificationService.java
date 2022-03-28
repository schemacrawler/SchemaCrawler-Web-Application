package us.fatehi.schemacrawler.webapp.service.notification;

import javax.validation.constraints.NotNull;

import us.fatehi.schemacrawler.webapp.model.DiagramRequest;

public interface NotificationService {

  void notify(DiagramRequest diagramRequest);
}