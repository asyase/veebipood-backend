package ee.asya.veebipoodbackend.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ee.asya.veebipoodbackend.smartpost.SmartPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/labels")
@RequiredArgsConstructor
public class LabelController {

    private final SmartPostService smartPostService;

    @Operation(summary = "Get a SmartPost shipping label",
            description = "Fetches the PDF shipping label for a shipment barcode (as returned by "
                    + "POST /api/orders/{orderId}/barcode) from SmartPost.")
    @ApiResponse(responseCode = "200", description = "Label PDF",
            content = @Content(mediaType = MediaType.APPLICATION_PDF_VALUE))
    @GetMapping(produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getLabel(
            @Parameter(description = "Shipment barcode to print a label for", required = true)
            @RequestParam String barcode,
            @Parameter(description = "Label size: A4/4, A4-4, A4-8, A4/8, A5, A6, A7")
            @RequestParam(defaultValue = "A5") String format) {
        byte[] pdf = smartPostService.getLabel(barcode, format);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + barcode + ".pdf\"")
                .body(pdf);
    }
}
