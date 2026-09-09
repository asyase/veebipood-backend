package ee.asya.veebipoodbackend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ee.asya.veebipoodbackend.dto.smartpost.ParcelTerminal;
import ee.asya.veebipoodbackend.smartpost.SmartPostService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/parcel-terminals")
@RequiredArgsConstructor
public class ParcelTerminalController {

    private final SmartPostService smartPostService;

    @GetMapping
    public List<ParcelTerminal> getAll(
            @RequestParam(defaultValue = "EE") String country,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String filter) {
        return smartPostService.getPlaces(country, type, filter);
    }
}
