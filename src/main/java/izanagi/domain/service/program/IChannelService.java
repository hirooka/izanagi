package izanagi.domain.service.program;

import izanagi.domain.entity.ChannelEntity;
import izanagi.domain.model.channel.Channel;
import java.util.List;
import java.util.Optional;

public interface IChannelService {

  ChannelEntity create(ChannelEntity channelEntity);

  Channel create(Channel channel);

  Optional<ChannelEntity> get(String id);

  ChannelEntity getChannelEntity(final int channelRemoteControl);

  List<ChannelEntity> getAll();

  ChannelEntity update(ChannelEntity channelEntity);

  Channel update(Channel channel);

  List<Channel> getChannelByChannelRemoteControl(int channelRemoteControl);

  List<Channel> getChannelByChannelRecording(int channelRecording);

  Channel getChannelByChannelRemoteControlNow(int channelRemoteControl);

  Channel getChannelByChannelRecordingNow(int channelRecording);

  List<Channel> getAllChannelsNow();

  void delete(Channel channel);

}
