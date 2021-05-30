package izanagi.app;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("izanagi")
@Controller
public class IzanagiController {

  @GetMapping("")
  public String izanagi() {
    return "izanagi";
  }
}
