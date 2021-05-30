package izanagi.domain.service.program;

import izanagi.domain.entity.ChannelEntity;
import izanagi.domain.model.channel.Channel;
import izanagi.domain.repository.program.IChannelRepository;
import izanagi.domain.repository.program.JpaSpecifications;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class ChannelService implements IChannelService {

  private final ModelMapper modelMapper;
  private final IChannelRepository channelRepository;

  @Override
  public ChannelEntity create(ChannelEntity channelEntity) {
    return channelRepository.save(channelEntity);
  }

  @Override
  public Channel create(Channel channel) {
    final ChannelEntity channelEntity = modelMapper.map(channel, ChannelEntity.class);
    final ChannelEntity createdChannelEntity = channelRepository.save(channelEntity);
    return modelMapper.map(createdChannelEntity, Channel.class);
  }

  @Override
  public Optional<ChannelEntity> get(String id) {
    final Specification<ChannelEntity> specification =
        Specification.where(JpaSpecifications.equalsChannel(id));
    return channelRepository.findOne(specification);
  }

  @Override
  public ChannelEntity getChannelEntity(final int channelRemoteControl) {
    return channelRepository.getFirstByChannelRemoteControl(channelRemoteControl);
  }

  @Override
  public List<ChannelEntity> getAll() {
    return channelRepository.findAll();
  }

  @Override
  public ChannelEntity update(ChannelEntity channelEntity) {
    return channelRepository.save(channelEntity);
  }

  @Override
  public Channel update(Channel channel) {
    return null;
  }

  @Override
  public List<Channel> getChannelByChannelRemoteControl(int channelRemoteControl) {
    return null;
  }

  @Override
  public List<Channel> getChannelByChannelRecording(int channelRecording) {
    return null;
  }

  @Override
  public Channel getChannelByChannelRemoteControlNow(int channelRemoteControl) {
    return null;
  }

  @Override
  public Channel getChannelByChannelRecordingNow(int channelRecording) {
    return null;
  }

  @Override
  public List<Channel> getAllChannelsNow() {
    return null;
  }

  @Override
  public void delete(Channel channel) {

  }
}
