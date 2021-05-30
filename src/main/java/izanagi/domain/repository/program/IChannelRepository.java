package izanagi.domain.repository.program;

import izanagi.domain.entity.ChannelEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface IChannelRepository
    extends JpaRepository<ChannelEntity, UUID>, JpaSpecificationExecutor<ChannelEntity> {
  ChannelEntity getFirstByChannelRemoteControl(final int channelRemoteControl);

  List<ChannelEntity> getAllByChannelRemoteControl(int channelRemoteControl);

  List<ChannelEntity> getAllByChannelRecording(int channelRecording);
}
