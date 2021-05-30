package izanagi.domain.model.tuner;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class TunerWrapper {
  @JsonProperty("tuner")
  private List<Tuner> tunerList;
}
