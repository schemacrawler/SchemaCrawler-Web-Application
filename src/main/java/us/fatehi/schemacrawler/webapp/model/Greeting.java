package us.fatehi.schemacrawler.webapp.model;


import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Greeting
{

  private long id;
  private String content;

  @NotNull
  @Size(min = 2, max = 30, message = "Please enter 2 through 30 characters")
  public String getContent()
  {
    return content;
  }

  @NotNull
  @Min(value = 1, message = "Please enter a greater than 0")
  @Max(value = 200, message = "Please enter a number less than 200")
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
