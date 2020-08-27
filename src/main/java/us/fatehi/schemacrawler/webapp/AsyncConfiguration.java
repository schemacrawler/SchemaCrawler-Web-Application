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
public class AsyncConfiguration
  extends AsyncConfigurerSupport
{

  private final static Logger logger =
    Logger.getLogger(AsyncConfiguration.class.getName());

  @Override
  public Executor getAsyncExecutor()
  {
    return new SimpleAsyncTaskExecutor();
  }

  @Override
  @Nullable

  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler()
  {
    return (throwable, method, parameters) -> {
      final StringBuilder buffer = new StringBuilder();
      buffer
        .append("Thread, " + Thread
          .currentThread()
          .getName())
        .append(System.lineSeparator());
      buffer
        .append("Method, " + method)
        .append(System.lineSeparator());
      buffer
        .append("Parameters, " + Arrays.asList(parameters))
        .append(System.lineSeparator());
      buffer
        .append(ExceptionUtils.getStackTrace(throwable))
        .append(System.lineSeparator());

      logger.warning(buffer.toString());
    };
  }

}
