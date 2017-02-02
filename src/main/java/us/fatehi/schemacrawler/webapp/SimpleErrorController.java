package us.fatehi.schemacrawler.webapp;


import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
@RequestMapping("/error")
public class SimpleErrorController
  implements ErrorController
{

  private final ErrorAttributes errorAttributes;

  @Autowired
  public SimpleErrorController(final ErrorAttributes errorAttributes)
  {
    Assert.notNull(errorAttributes, "ErrorAttributes must not be null");
    this.errorAttributes = errorAttributes;
  }

  @RequestMapping
  public Map<String, Object> error(final HttpServletRequest aRequest)
  {
    final Map<String, Object> body = getErrorAttributes(aRequest,
                                                        getTraceParameter(aRequest));
    final String trace = (String) body.get("trace");
    if (trace != null)
    {
      final String[] lines = trace.split("\n\t");
      body.put("trace", lines);
    }
    return body;
  }

  @Override
  public String getErrorPath()
  {
    return "/error";
  }

  private Map<String, Object> getErrorAttributes(final HttpServletRequest aRequest,
                                                 final boolean includeStackTrace)
  {
    final RequestAttributes requestAttributes = new ServletRequestAttributes(aRequest);
    return errorAttributes.getErrorAttributes(requestAttributes,
                                              includeStackTrace);
  }

  private boolean getTraceParameter(final HttpServletRequest request)
  {
    final String parameter = request.getParameter("trace");
    if (parameter == null)
    {
      return false;
    }
    return !"false".equals(parameter.toLowerCase());
  }
}
