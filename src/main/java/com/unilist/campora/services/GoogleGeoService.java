package com.unilist.campora.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class GoogleGeoService {
    @Value("${google.geo.api.key}")
    private String geoApiKey;

    public double[] getCoordinatesFromPostalCode(String postalCode){
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address="
                + postalCode + "&key=" + geoApiKey;

        RestTemplate restTemplate = new RestTemplate();
        Map res = restTemplate.getForObject(url,Map.class);
        List results = (List) res.get("results");
        if(results == null || results.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid postal code");
        }
        Map geometry = (Map)((Map)results.get(0)).get("geometry");
        Map location = (Map) geometry.get("location");
        double lat = ((Number)location.get("lat")).doubleValue();
        double lng = ((Number)location.get("lng")).doubleValue();
        return new double[] {lat,lng};
    }
}
