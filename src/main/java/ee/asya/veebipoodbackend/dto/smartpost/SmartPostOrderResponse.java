package ee.asya.veebipoodbackend.dto.smartpost;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SmartPostOrderResponse(@JsonProperty("orders") Orders orders) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Orders(@JsonProperty("item") List<Item> item) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(String barcode, String reference) {
    }
}
