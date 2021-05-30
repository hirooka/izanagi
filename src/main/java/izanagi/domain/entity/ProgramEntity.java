package izanagi.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
public class ProgramEntity extends AbstractEntity {

  @Getter
  @Setter
  private String id;

  //private String channel;

  @Getter
  @Setter
  private String title; // epgdump title

  @Getter
  @Setter
  private String detail; // epgdump detail

  //    private List<Item> extdetail;

  @Getter
  @Setter
  private long start; // epgdump start

  @Getter
  @Setter
  @Column(name = "\"end\"")
  private long end; // epgdump end

  @Getter
  @Setter
  private long duration; // epgdump duration

  //  private List<Category> category;
  //  private List<?> attachinfo;
  //  private Video video;
  //  private List<Audio> audio;
  //  private boolean freeCA;

  @Getter
  @Setter
  private int eventId; // epgdump event_id

  @Getter
  @Setter
  private long begin;

  @Getter
  @Setter
  private String beginDate;

  @Getter
  @Setter
  private String endDate;

  //private int channelRecording;
  //private int channelRemoteControl;

  @Getter
  @Setter
  @ManyToOne
  private ChannelEntity channelEntity;
}
