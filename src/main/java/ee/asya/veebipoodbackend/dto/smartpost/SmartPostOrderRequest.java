package ee.asya.veebipoodbackend.dto.smartpost;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SmartPostOrderRequest(@JsonProperty("orders") Orders orders) {

    public record Orders(@JsonProperty("item") Item item) {
    }

    public record Item(Source source, Recipient recipient, Destination destination) {
    }

    public record Source(String country) {
    }

    public record Recipient(String name, String phone) {
    }

    public record Destination(@JsonProperty("place_id") String placeId) {
    }
}
