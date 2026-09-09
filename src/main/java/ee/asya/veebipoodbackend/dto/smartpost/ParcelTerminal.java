package ee.asya.veebipoodbackend.dto.smartpost;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ParcelTerminal(
        @JsonProperty("place_id") String placeId,
        String name,
        String city,
        String address,
        String country,
        @JsonProperty("postalcode") String postalCode,
        @JsonProperty("routingcode") String routingCode,
        String availability,
        String description,
        String type,
        String lat,
        String lng,
        @JsonProperty("group_id") Integer groupId,
        @JsonProperty("group_name") String groupName,
        @JsonProperty("group_sort") Integer groupSort,
        @JsonProperty("created_date") String createdDate,
        @JsonProperty("updated_date") String updatedDate,
        @JsonProperty("is_outdoor") String isOutdoor) {
}
