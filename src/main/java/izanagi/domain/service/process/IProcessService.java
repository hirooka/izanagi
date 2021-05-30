package izanagi.domain.service.process;

import izanagi.domain.model.tuner.TunerType;
import java.util.List;
import java.util.Optional;

public interface IProcessService {
  Optional<Process> create(Process process);

  Process read(TunerType tunerType, int channel);

  List<Process> readAll();

  Optional<Process> readByPid(long pid);

  Process update(Process process);

  void delete(Process process);

  void delete(long pid);
}
