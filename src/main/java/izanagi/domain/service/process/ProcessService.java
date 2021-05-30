package izanagi.domain.service.process;

import izanagi.domain.model.tuner.TunerType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProcessService implements IProcessService {

  private List<Process> processList = new ArrayList<>();

  @Override
  public Optional<Process> create(Process process) {
    if (processList.stream()
        .map(Process::pid).collect(Collectors.toList()).contains(process.pid())) {
      throw new IllegalStateException("");
    } else {
      processList.add(process);
    }
    return readByPid(process.pid());
  }

  @Override
  public Process read(TunerType tunerType, int channel) {
    return null;
  }

  @Override
  public List<Process> readAll() {
    return processList;
  }

  @Override
  public Optional<Process> readByPid(long pid) {
    return processList.stream()
        .filter(process -> process.pid() == pid)
        .reduce((t1, t2) -> {
          throw new IllegalStateException("duplicated " + t1.toString() + ", " + t2.toString());
        });
  }

  @Override
  public Process update(Process process) {
    return null;
  }

  @Override
  public void delete(Process process) {

  }

  @Override
  public void delete(long pid) {
    final List<Process> toBeRemovedProcessList = new ArrayList<>();
    processList.stream()
        .filter(process -> process.pid() == pid).forEach(toBeRemovedProcessList::add);
    toBeRemovedProcessList.forEach(Process::destroy);
    processList.removeIf(toBeRemovedProcessList::contains);
  }
}
