package us.fatehi.schemacrawler.webapp.model;


import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SchemaCrawlerSQLiteDiagramRequest
  implements Serializable
{

  private static final long serialVersionUID = 2065519510282344200L;
  private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

  private String name;
  private String email;
  private String key;

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj)
  {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @NotNull
  // @Pattern(regexp =
  // "\\A[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\z",
  // message = "Please enter a valid email address")
  public String getEmail()
  {
    return email;
  }

  public String getKey()
  {
    return key;
  }

  @NotNull
  @Size(min = 2, message = "Please enter your full name")
  public String getName()
  {
    return name;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    return HashCodeBuilder.reflectionHashCode(this, false);
  }

  public void setEmail(final String email)
  {
    this.email = email;
  }

  public void setKey(final String key)
  {
    this.key = key;
  }

  public void setName(final String name)
  {
    this.name = name;
  }

  @Override
  public String toString()
  {
    return gson.toJson(this);
  }

}
