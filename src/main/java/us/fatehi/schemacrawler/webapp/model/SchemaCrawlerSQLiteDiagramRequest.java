package us.fatehi.schemacrawler.webapp.model;


import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class SchemaCrawlerSQLiteDiagramRequest
  implements Serializable
{

  private static final long serialVersionUID = 2065519510282344200L;

  private String name;
  private String email;

  @Override
  public boolean equals(final Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    final SchemaCrawlerSQLiteDiagramRequest other = (SchemaCrawlerSQLiteDiagramRequest) obj;
    if (email == null)
    {
      if (other.email != null)
      {
        return false;
      }
    }
    else if (!email.equals(other.email))
    {
      return false;
    }
    if (name == null)
    {
      if (other.name != null)
      {
        return false;
      }
    }
    else if (!name.equals(other.name))
    {
      return false;
    }
    return true;
  }

  @NotNull
  // @Pattern(regexp =
  // "\\A[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\z",
  // message = "Please enter a valid email address")
  public String getEmail()
  {
    return email;
  }

  @NotNull
  @Size(min = 2, message = "Please enter your full name")
  public String getName()
  {
    return name;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (email == null? 0: email.hashCode());
    result = prime * result + (name == null? 0: name.hashCode());
    return result;
  }

  public void setEmail(final String email)
  {
    this.email = email;
  }

  public void setName(final String name)
  {
    this.name = name;
  }

  @Override
  public String toString()
  {
    return "SchemaCrawlerSQLiteDiagramRequest [name=" + name + ", email="
           + email + "]";
  }

}
