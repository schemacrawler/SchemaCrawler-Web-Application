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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Entity
public class SchemaCrawlerSQLiteDiagramRequest
  implements Serializable
{

  private static final long serialVersionUID = 2065519510282344200L;

  private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @NotNull
  @Size(min = 2, message = "Please enter your full name")
  private String name;

  @NotNull
  @Pattern(regexp = "\\A[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\z", message = "Please enter a valid email address")
  private String email;

  @Column(unique = true)
  private final String key;

  private final LocalDateTime timestamp;

  public SchemaCrawlerSQLiteDiagramRequest()
  {
    timestamp = LocalDateTime.now();
    key = RandomStringUtils.randomAlphanumeric(12).toLowerCase();
  }

  @Override
  public boolean equals(final Object obj)
  {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  public String getEmail()
  {
    return email;
  }

  public Long getId()
  {
    return id;
  }

  public String getKey()
  {
    return key;
  }

  public String getName()
  {
    return name;
  }

  public LocalDateTime getTimestamp()
  {
    return timestamp;
  }

  @Override
  public int hashCode()
  {
    return HashCodeBuilder.reflectionHashCode(this, false);
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
    return gson.toJson(this);
  }

}
