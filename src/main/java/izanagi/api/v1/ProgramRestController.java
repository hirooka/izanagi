package izanagi.api.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import izanagi.domain.model.Hello;
import izanagi.domain.model.program.Program;
import izanagi.domain.operator.program.IProgramOperator;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SecurityRequirement(name = "izanagi")
@Slf4j
@AllArgsConstructor
@RequestMapping("api/v1/programs")
@RestController
public class ProgramRestController {

  private final IProgramOperator programOperator;

  @Operation(summary = "by Channel")
  @GetMapping("{channelRemoteControl}")
  public List<Program> getProgramListByChannelRemoteControl(
      @Parameter(description = "Channel", required = true)
      @PathVariable final int channelRemoteControl
  ) {
    return programOperator.getByChannelRemoteControl(channelRemoteControl);
  }

  @Operation(summary = "by Channel (Live)")
  @GetMapping("{channelRemoteControl}/now")
  public Program getProgramByChannelNow(
      @Parameter(description = "Channel", required = true)
      @PathVariable final int channelRemoteControl
  ) {
    return programOperator.getByChannelRemoteControlNow(channelRemoteControl);
  }

  @Operation(summary = "Live")
  @GetMapping("now")
  public List<Program> getProgramListNow() {
    return programOperator.getAllNow();
  }

  @PostMapping("")
  public Hello acquire() {
    log.info("Acquire EPG asynchronously");
    programOperator.acquireEpg();
    final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
    final Instant instant = Instant.now();
    final Hello hello = new Hello();
    hello.setDate(instant.atZone(ZoneId.systemDefault()).format(dateTimeFormatter));
    hello.setEpoch(instant.toEpochMilli());
    return hello;
  }
}
