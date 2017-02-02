package us.fatehi.schemacrawler.webapp.storage;


public class StorageException
  extends RuntimeException
{

  private static final long serialVersionUID = -5649399097209045856L;

  public StorageException(final String message)
  {
    super(message);
  }

  public StorageException(final String message, final Throwable cause)
  {
    super(message, cause);
  }
}
