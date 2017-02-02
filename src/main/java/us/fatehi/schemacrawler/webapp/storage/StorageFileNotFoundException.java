package us.fatehi.schemacrawler.webapp.storage;


public class StorageFileNotFoundException
  extends StorageException
{

  private static final long serialVersionUID = -3361061561699246152L;

  public StorageFileNotFoundException(final String message)
  {
    super(message);
  }

  public StorageFileNotFoundException(final String message, final Throwable cause)
  {
    super(message, cause);
  }
}
