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
package us.fatehi.schemacrawler.webapp;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
public class SchemaCrawlerWebApplication
  extends WebMvcConfigurerAdapter
  implements CommandLineRunner
{

  /**
   * Spring Boot entry point.
   *
   * @param args
   *        Spring Boot arguments.
   */
  public static void main(final String[] args)
  {
    SpringApplication.run(SchemaCrawlerWebApplication.class, args);
  }


  @Override
  public void addFormatters(final FormatterRegistry registry)
  {
    registry.addConverter(new MultipartFileConverter());
  }

  @Override
  public void run(final String... args)
    throws Exception
  {
    // Do something, if needed
  }

}
