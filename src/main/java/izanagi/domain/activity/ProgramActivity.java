package izanagi.domain.activity;

import izanagi.domain.config.EpgdumpConfiguration;
import izanagi.domain.operator.program.IProgramOperator;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class ProgramActivity implements IProgramActivity {

  private final EpgdumpConfiguration epgdumpConfiguration; // TODO: separate program/epgdump
  private final IProgramOperator programOperator;

  private boolean canExecute() {
    final long lastExecuted = programOperator.getLatest();
    final long interval = Instant.now().toEpochMilli() - lastExecuted;
    if (interval > epgdumpConfiguration.getEpgdumpExecuteOnBootIgnoreInterval()) {
      return true;
    } else {
      final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
      final String lastExecutedDate =
          Instant.ofEpochMilli(lastExecuted).atZone(ZoneId.systemDefault())
              .format(dateTimeFormatter);
      log.warn("epgdump was not executed. Last executed: {}", lastExecutedDate);
      return false;
    }
  }

  @PostConstruct
  void init() {
    if (canExecute()) {
      programOperator.acquireEpg();
    }
  }

  @Scheduled(cron = "${epgdump.epgdump-execute-schedule-cron}")
  void cron() {
    log.info("Scheduled epgdump execution");
    programOperator.acquireEpg();
  }

  @Override
  public void acquireEpg() { // TODO: move to operator w/ check
    if (canExecute()) {
      programOperator.acquireEpg();
    } // TODO: throw ex
  }
}
