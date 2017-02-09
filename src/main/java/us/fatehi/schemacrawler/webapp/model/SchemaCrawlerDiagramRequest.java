/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package us.fatehi.schemacrawler.webapp.model;


import java.io.Serializable;
import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SchemaCrawlerDiagramRequest
  implements Serializable
{

  private static final long serialVersionUID = 2065519510282344200L;

  private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

  /**
   * Factory method to deserialize a JSON request.
   *
   * @param jsonRequest
   *        JSON serialized request.
   * @return Deserialzied Java request.
   */
  public static SchemaCrawlerDiagramRequest fromJson(final String jsonRequest)
  {
    if (StringUtils.isBlank(jsonRequest))
    {
      return null;
    }
    return gson.fromJson(jsonRequest, SchemaCrawlerDiagramRequest.class);
  }

  @NotNull
  @Size(min = 2, message = "Please enter your full name")
  private String name;

  @NotNull
  @Pattern(regexp = "\\A[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\z", message = "Please enter a valid email address")
  private String email;

  @NotNull
  @Size(min = 1, message = "Please select a file to upload")
  private String file;

  private final String key;

  private final LocalDateTime timestamp;

  /**
   * Public constructor. Generates a random key, and sets the creation
   * timestamp.
   */
  public SchemaCrawlerDiagramRequest()
  {
    timestamp = LocalDateTime.now();
    key = RandomStringUtils.randomAlphanumeric(12).toLowerCase();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj)
  {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  /**
   * Returns the email address of the requester.
   *
   * @return Email address of the requester.
   */
  public String getEmail()
  {
    return email;
  }

  /**
   * Returns the uploaded file name.
   *
   * @return Uploaded file name.
   */
  public String getFile()
  {
    return file;
  }

  /**
   * Returns a randomized unique key for the request.
   *
   * @return Unique key for the request.
   */
  public String getKey()
  {
    return key;
  }

  /**
   * Returns the name of the requester.
   *
   * @return Name of the requester.
   */
  public String getName()
  {
    return name;
  }

  /**
   * Returns the request creation timestamp.
   *
   * @return Request creation timestamp.
   */
  public LocalDateTime getTimestamp()
  {
    return timestamp;
  }

  /**
   * {@inheritDoc}
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

  /**
   * Sets the uploaded file name.
   *
   * @param file
   *        Uploaded file name.
   */
  public void setFile(final String file)
  {
    this.file = file;
  }

  /**
   * Sets the name of the requester.
   *
   * @param name
   *        Name of the requester.
   */
  public void setName(final String name)
  {
    this.name = name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    return gson.toJson(this);
  }

}
