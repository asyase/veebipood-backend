package ee.asya.veebipoodbackend.dto.smartpost;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SmartPostPlacesResponse(@JsonProperty("places") Places places) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Places(@JsonProperty("item") List<ParcelTerminal> item) {
    }
}
