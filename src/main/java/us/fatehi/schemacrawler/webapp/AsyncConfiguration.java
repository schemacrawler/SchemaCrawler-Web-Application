/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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

import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfiguration extends AsyncConfigurerSupport {

  private static final Logger logger = Logger.getLogger(AsyncConfiguration.class.getName());

  @Override
  public Executor getAsyncExecutor() {
    return new SimpleAsyncTaskExecutor();
  }

  @Override
  @Nullable
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    return (throwable, method, parameters) -> {
      final StringBuilder buffer = new StringBuilder();
      buffer.append("Thread, " + Thread.currentThread().getName()).append(System.lineSeparator());
      buffer.append("Method, " + method).append(System.lineSeparator());
      buffer.append("Parameters, " + Arrays.asList(parameters)).append(System.lineSeparator());
      buffer.append(ExceptionUtils.getStackTrace(throwable)).append(System.lineSeparator());

      logger.warning(buffer.toString());
    };
  }
}
