package izanagi.api.v1;

import izanagi.domain.model.Hello;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class IndexRestController {
  @GetMapping("api")
  public Hello hello() {
    final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
    final Instant instant = Instant.now();
    final Hello hello = new Hello();
    hello.setDate(instant.atZone(ZoneId.systemDefault()).format(dateTimeFormatter));
    hello.setEpoch(instant.toEpochMilli());
    return hello;
  }
}
