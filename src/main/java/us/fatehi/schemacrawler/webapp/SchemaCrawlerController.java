package us.fatehi.schemacrawler.webapp;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import us.fatehi.schemacrawler.webapp.model.Greeting;

@Controller
public class SchemaCrawlerController
{

  @GetMapping("/greeting")
  public String greetingForm(final Model model)
  {
    model.addAttribute("greeting", new Greeting());
    return "greeting";
  }

  @PostMapping("/greeting")
  public String greetingSubmit(@ModelAttribute final Greeting greeting)
  {
    return "result";
  }

}
