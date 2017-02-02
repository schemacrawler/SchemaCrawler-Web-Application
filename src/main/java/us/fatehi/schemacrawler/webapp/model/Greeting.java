package us.fatehi.schemacrawler.webapp.model;


public class Greeting
{

  private long id;
  private String content;

  public String getContent()
  {
    return content;
  }

  public long getId()
  {
    return id;
  }

  public void setContent(final String content)
  {
    this.content = content;
  }

  public void setId(final long id)
  {
    this.id = id;
  }

}
