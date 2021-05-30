package izanagi.domain.service.epg;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import izanagi.domain.entity.ChannelEntity;
import izanagi.domain.entity.ProgramEntity;
import izanagi.domain.model.channel.Channel;
import izanagi.domain.model.channel.ChannelConfiguration;
import izanagi.domain.model.epg.EpgdumpChannel;
import izanagi.domain.model.program.Program;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EpgdumpJsonParser {

  private static int getChannelRecordingByServiceId(
      int serviceId, List<ChannelConfiguration> channelConfigurationList
  ) {
    for (ChannelConfiguration channelConfiguration : channelConfigurationList) {
      if (serviceId == channelConfiguration.getServiceId()) {
        return channelConfiguration.getChannelRecording();
      }
    }
    return -1;
  }

  private static int getChannelRemoteControlByServiceId(
      int serviceId, List<ChannelConfiguration> channelConfigurationList
  ) {
    for (ChannelConfiguration channelConfiguration : channelConfigurationList) {
      if (serviceId == channelConfiguration.getServiceId()) {
        return channelConfiguration.getChannelRemoteControl();
      }
    }
    return -1;
  }

  private static boolean isUnknownServiceId(
      int serviceId, List<ChannelConfiguration> channelConfigurationList
  ) {
    for (ChannelConfiguration channelConfiguration : channelConfigurationList) {
      if (serviceId == channelConfiguration.getServiceId()) {
        return false;
      }
    }
    return true;
  }

  private static ChannelEntity getById(List<ChannelEntity> channelEntityList, String id) {
    for (ChannelEntity channelEntity : channelEntityList) {
      if (channelEntity.getId().equals(id)) {
        return channelEntity;
      }
    }
    return null;
  }

  public static List<ChannelEntity> parseJson(
      final String path,
      final List<ChannelConfiguration> channelConfigurationList,
      final List<ChannelEntity> channelEntityList
  ) {
    final List<Integer> eventIdList = new ArrayList<>();
    channelEntityList.forEach(channelEntity -> {
      channelEntity.getProgramEntityList().forEach(programEntity -> {
        eventIdList.add(programEntity.getEventId());
      });
    });

    try {
      final BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
      final String jsonString = bufferedReader.readLine();

      final ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE); // TODO: ja_JP
      final List<EpgdumpChannel> epgdumpChannelList =
          objectMapper.readValue(jsonString, new TypeReference<>() {});

      final List<ChannelEntity> updatedChannelEntityList = new ArrayList<>();
      epgdumpChannelList.forEach(epgdumpChannel -> {
        if (epgdumpChannel.getId().startsWith("GR")
            && isUnknownServiceId(epgdumpChannel.getServiceId(), channelConfigurationList)
        ) {
          log.warn("Unknown serviceId : {} : {}",
              epgdumpChannel.getId(), epgdumpChannel.getServiceId()
          );
        } else {
          final ChannelEntity channelEntity;
          if (getById(channelEntityList, epgdumpChannel.getId()) == null) {
            channelEntity = new ChannelEntity();
            channelEntity.setId(epgdumpChannel.getId());
          } else {
            channelEntity = getById(channelEntityList, epgdumpChannel.getId());
          }
          channelEntity.setTransportStreamId(epgdumpChannel.getTransportStreamId());
          channelEntity.setOriginalNetworkId(epgdumpChannel.getOriginalNetworkId());
          channelEntity.setServiceId(epgdumpChannel.getServiceId());
          channelEntity.setName(epgdumpChannel.getName());
          if (epgdumpChannel.getId().startsWith("GR")) {
            channelEntity.setChannelRecording(
                getChannelRecordingByServiceId(epgdumpChannel.getServiceId(),
                    channelConfigurationList)
            );
            channelEntity.setChannelRemoteControl(
                getChannelRemoteControlByServiceId(epgdumpChannel.getServiceId(),
                    channelConfigurationList)
            );
          } else if (epgdumpChannel.getId().startsWith("BS")) {
            channelEntity.setChannelRecording(epgdumpChannel.getServiceId());
            channelEntity.setChannelRemoteControl(epgdumpChannel.getServiceId());
          }

          final List<ProgramEntity> updatedProgramEntityList = new ArrayList<>();
          epgdumpChannel.getPrograms().forEach(epgdumpProgram -> {
            if (epgdumpChannel.getId().equals(epgdumpProgram.getChannel())) {
              if (eventIdList.contains(epgdumpProgram.getEventId())) {
                // TODO: update program...
                log.debug("duplicated event ID {}", epgdumpProgram.getEventId());
              } else {
                final ProgramEntity programEntity = new ProgramEntity();
                if (epgdumpProgram.getChannel().startsWith("GR")) {
                  // TODO: yyMMdd
                  programEntity.setId(epgdumpProgram.getChannel() + "_"
                      + epgdumpProgram.getStart() + "_"
                      + epgdumpProgram.getEventId());
                  programEntity.setTitle(epgdumpProgram.getTitle());
                  programEntity.setDetail(epgdumpProgram.getDetail());
                  final long begin = epgdumpProgram.getStart();
                  final long end = epgdumpProgram.getEnd();
                  programEntity.setBegin(begin);
                  programEntity.setEnd(end);
                  programEntity.setDuration(epgdumpProgram.getDuration());
                  programEntity.setBeginDate(convertMilliToDate(begin));
                  programEntity.setEndDate(convertMilliToDate(end));
                  programEntity.setEventId(epgdumpProgram.getEventId());
                  programEntity.setChannelEntity(channelEntity);
                  updatedProgramEntityList.add(programEntity);
                } else if (epgdumpProgram.getChannel().startsWith("BS_")) {
                  programEntity.setId(epgdumpProgram.getChannel() + "_"
                      + epgdumpProgram.getStart() + "_"
                      + epgdumpProgram.getEventId());
                  programEntity.setTitle(epgdumpProgram.getTitle());
                  programEntity.setDetail(epgdumpProgram.getDetail());
                  final long begin = epgdumpProgram.getStart();
                  final long end = epgdumpProgram.getEnd();
                  programEntity.setBegin(begin);
                  programEntity.setEnd(end);
                  programEntity.setDuration(epgdumpProgram.getDuration());
                  programEntity.setBeginDate(convertMilliToDate(begin));
                  programEntity.setEndDate(convertMilliToDate(end));
                  programEntity.setEventId(epgdumpProgram.getEventId());
                  programEntity.setChannelEntity(channelEntity);
                  updatedProgramEntityList.add(programEntity);
                } else {
                  log.warn("Unknown channel: {}", epgdumpProgram.getChannel());
                }
              }
            }
          });
          channelEntity.setProgramEntityList(updatedProgramEntityList);
          updatedChannelEntityList.add(channelEntity);
        }
      });
      return updatedChannelEntityList;
    } catch (IOException ex) {
      log.error(ex.getMessage());
      return new ArrayList<>();
    }
  }

  public static List<Program> parse(
      String path, int channelRecording, int channelRemoteControl, List<Program> existingProgramList
  ) {

    final List<Program> programList = new ArrayList<>();

    final List<Integer> eventIdList = existingProgramList.stream()
        .map(Program::getEventId).collect(Collectors.toList());

    try {

      final BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(path)));
      final String jsonString = bufferedReader.readLine();

      final ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
      final List<Channel> channelList =
          objectMapper.readValue(jsonString, new TypeReference<List<Channel>>() {});
      channelList.forEach(channel -> {
        channel.getPrograms().forEach(program -> {
          if (eventIdList.contains(program.getEventId())) {
            // update program...
            log.info("duplicated event ID {}", program.getEventId());
          } else {
            if (program.getChannel().startsWith("GR")) {
              program.setId(channelRecording + "_" + program.getStart());
              //                            program.setChannelRecording(channelRecording);
              //                            program.setChannelRemoteControl(channelRemoteControl);
              //                            program.setChannelName(channel.getName());
              final long begin = program.getStart() / 10;
              final long end = program.getEnd() / 10;
              program.setBegin(begin);
              program.setStart(begin);
              program.setEnd(end);
              program.setBeginDate(convertMilliToDate(begin));
              program.setEndDate(convertMilliToDate(end));
              programList.add(program);
            } else if (program.getChannel().startsWith("BS_")) {
              try {
                final int bsChannelRemoteControl =
                    Integer.parseInt(program.getChannel().split("BS_")[1]);
                program.setId(bsChannelRemoteControl + "_" + program.getStart());
                //program.setChannelRecording(bsChannelRemoteControl);
                //program.setChannelRemoteControl(bsChannelRemoteControl);
                //program.setChannelName(channel.getName());
                final long begin = program.getStart() / 10;
                final long end = program.getEnd() / 10;
                program.setBegin(begin);
                program.setStart(begin);
                program.setEnd(end);
                program.setBeginDate(convertMilliToDate(begin));
                program.setEndDate(convertMilliToDate(end));
                programList.add(program);
              } catch (NumberFormatException e) {
                log.error("invalid channel", e.getMessage(), e);
              }
            } else {
              log.info("program.getChannel is not GR|BS.");
            }
          }
        });
      });


    } catch (IOException e) {
      e.printStackTrace();
    }
    return programList;
  }

  private static String convertMilliToDate(long milli) {
    final Instant instant = Instant.ofEpochMilli(milli);
    final ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
    final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
    return zonedDateTime.format(dateTimeFormatter);
  }
}
