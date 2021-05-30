package izanagi.domain.service.program;

import izanagi.domain.entity.ProgramEntity;
import izanagi.domain.model.program.Program;
import izanagi.domain.repository.program.IProgramRepository;
import izanagi.domain.repository.program.JpaSpecifications;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class ProgramService implements IProgramService {

  private final ModelMapper modelMapper;
  private final IProgramRepository programRepository;

  @Override
  public Program create(Program program) {
    final ProgramEntity programEntity = modelMapper.map(program, ProgramEntity.class);
    final ProgramEntity createdProgramEntity = programRepository.save(programEntity);
    return modelMapper.map(createdProgramEntity, Program.class);
  }

  @Override
  public Program create(ProgramEntity programEntity) {
    final ProgramEntity createdProgramEntity = programRepository.save(programEntity);
    return modelMapper.map(createdProgramEntity, Program.class);
  }

  @Override
  public List<Program> readByChannelRemoteControl(int channelRemoteControl) {
    final List<ProgramEntity> programEntityList = null;
    final List<Program> programList = new ArrayList<>();
    programEntityList.forEach(programEntity -> {
      Program program = new Program();
      modelMapper.map(programEntity, program);
      programList.add(program);
    });
    return programList;
  }

  @Override
  public List<Program> readByChannelRecording(int channelRecording) {
    final List<ProgramEntity> programEntityList = new ArrayList<>();
    final List<Program> programList = new ArrayList<>();
    programEntityList.forEach(programEntity -> {
      final Program program = new Program();
      modelMapper.map(programEntity, program);
      programList.add(program);
    });
    return programList;
  }

  @Override
  public Program readByChannelRemoteControlNow(int channelRemoteControl) {
    final long now = Instant.now().toEpochMilli();
    final Specification<ProgramEntity> specification =
        Specification.where(
            JpaSpecifications.isLive(now)
                .and(JpaSpecifications.equalsChannelRemoteControl(channelRemoteControl)));
    final Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
    final Page<ProgramEntity> programEntityPage =
        programRepository.findAll(specification, pageable);
    final List<Program> programList = new ArrayList<>();
    programEntityPage.forEach(programEntity -> {
      final Program program = new Program();
      modelMapper.map(programEntity, program);
      program.setChannelRemoteControl(programEntity.getChannelEntity().getChannelRemoteControl());
      program.setChannelRecording(programEntity.getChannelEntity().getChannelRecording());
      program.setChannelName(programEntity.getChannelEntity().getName());
      programList.add(program);
    });
    if (programList.size() == 1) {
      return programList.get(0);
    } else {
      return new Program();
    }
  }

  @Override
  public Program readByChannelRecordingNow(int channelRecording) {
    return null;
  }

  @Override
  public List<Program> readAllNow() {
    final long now = System.currentTimeMillis();
    final List<ProgramEntity> programEntityList =
        programRepository.findByBeginLessThanAndEndGreaterThan(now, now);
    final List<Program> programList = new ArrayList<>();
    programEntityList.forEach(programEntity -> {
      Program program = new Program();
      modelMapper.map(programEntity, program);
      programList.add(program);
    });
    return programList;
  }

  @Override
  public Program update(Program program) {
    final ProgramEntity programEntity =
        programRepository.findById(program.getUuid()).orElse(new ProgramEntity());
    modelMapper.map(program, programEntity);
    final ProgramEntity updatedProgramEntity = programRepository.save(programEntity);
    return modelMapper.map(updatedProgramEntity, Program.class);
  }

  @Override
  public void delete(Program program) {
    programRepository.deleteById(program.getUuid());
  }

  @Override
  public List<Program> getAllNow() {
    final long now = Instant.now().toEpochMilli();
    final Specification<ProgramEntity> specification =
        Specification.where(JpaSpecifications.isLive(now));
    final Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
    final Page<ProgramEntity> programEntityPage =
        programRepository.findAll(specification, pageable);
    final List<Program> programList = new ArrayList<>();
    programEntityPage.forEach(programEntity -> {
      final Program program = new Program();
      modelMapper.map(programEntity, program);
      program.setChannelRemoteControl(programEntity.getChannelEntity().getChannelRemoteControl());
      program.setChannelRecording(programEntity.getChannelEntity().getChannelRecording());
      program.setChannelName(programEntity.getChannelEntity().getName());
      programList.add(program);
    });
    programList.sort(Comparator.comparingInt(Program::getChannelRemoteControl));
    return programList;
  }

  @Override
  public List<Program> getAllByChannelRemoteControl(int channelRemoteControl) {
    final Specification<ProgramEntity> specification =
        Specification.where(JpaSpecifications.equalsChannelRemoteControl(channelRemoteControl));
    final Pageable pageable =
        PageRequest.of(0, Integer.MAX_VALUE, Sort.by("begin").ascending());
    final Page<ProgramEntity> programEntityPage =
        programRepository.findAll(specification, pageable);
    final List<Program> programList = new ArrayList<>();
    programEntityPage.forEach(programEntity -> {
      final Program program = new Program();
      modelMapper.map(programEntity, program);
      program.setChannelRemoteControl(programEntity.getChannelEntity().getChannelRemoteControl());
      program.setChannelRecording(programEntity.getChannelEntity().getChannelRecording());
      program.setChannelName(programEntity.getChannelEntity().getName());
      programList.add(program);
    });
    return programList;
  }
}
