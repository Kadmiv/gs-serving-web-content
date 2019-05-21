package com.biling.controllers;

import com.biling.repo.models.SystemPolygon;
import com.biling.repo.models.request.Request;
import com.biling.repo.models.request.RequestTrack;
import com.biling.repo.models.responce.Response;
import com.biling.repo.models.responce.ResponseTrack;
import com.google.gson.Gson;
import javafx.beans.binding.DoubleExpression;
import org.geojson.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Policy;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {

    @RequestMapping(value = "/doCalculate", method = RequestMethod.POST)
    public ResponseEntity<Response> doCalculate(@RequestBody Request requestData) {
        if (requestData == null) {
            return ResponseEntity.notFound().build();
        } else {
            Response responseData = new Response();
            ArrayList<ResponseTrack> list = new ArrayList<>();
            list.add(new ResponseTrack());
            list.add(new ResponseTrack());
            list.add(new ResponseTrack());
            list.add(new ResponseTrack());
            list.add(new ResponseTrack());
            list.add(new ResponseTrack());
            responseData.fullTrack = list;
            return ResponseEntity.ok(responseData);
        }
    }


    @RequestMapping(value = "/toGeoJson", method = RequestMethod.POST)
    public ResponseEntity<GeoJsonObject> toGeoJson(@RequestBody List<SystemPolygon> data) {
        if (data == null) {
            return ResponseEntity.notFound().build();
        }
        FeatureCollection responseData = new FeatureCollection();
        for (SystemPolygon item : data) {
            Polygon newP = new Polygon();

            List<List<List<Double>>> coords = item.getCoords();
            List<List<LngLatAlt>> pol_coords = new ArrayList<>();
            for (List<List<Double>> list : coords) {
                List<LngLatAlt> part = new ArrayList<>();
                for (List<Double> lat_lon : list) {
                    LngLatAlt newL = new LngLatAlt();
                    newL.setLatitude(lat_lon.get(0));
                    newL.setLongitude(lat_lon.get(1));
                    part.add(newL);
                }
                pol_coords.add(part);
            }

            newP.setCoordinates(pol_coords);
            Feature feat = new Feature();
            feat.setGeometry(newP);
            responseData.add(feat);
        }

        return ResponseEntity.ok(responseData);
    }

    @RequestMapping(value = "/doCalculateWithGeoJson", method = RequestMethod.POST)
    public ResponseEntity<List<ResponseTrack>> doCalculateWithGeoJson(@RequestBody GeoJsonObject data) {
        if (data == null) {
            return ResponseEntity.notFound().build();
        } else {

            ArrayList<Polygon> polygons = new ArrayList<>();
            ArrayList<LineString> track = new ArrayList<>();
            if (data instanceof FeatureCollection) {
                for (Feature item : ((FeatureCollection) data).getFeatures()) {
//            item.getProperties();
                    GeoJsonObject geometry = item.getGeometry();
                    if (geometry instanceof Polygon) {
                        polygons.add((Polygon) geometry);
//                        System.out.println("Polygon:\n" + geometry.toString());
                    } else if (geometry instanceof LineString) {
                        track.add((LineString) geometry);
//                        System.out.println("Track:\n" + geometry.toString());
                    }
                }
            }


            int count = 1;
            for (Polygon polygon : polygons) {
                SystemPolygon cPolygon = new SystemPolygon();
                cPolygon.setName("00" + count);

                List<List<List<Double>>> coords = new ArrayList<>();
                for (List<LngLatAlt> list : polygon.getCoordinates()) {
                    List<List<Double>> array = new ArrayList<>();
                    coords.add(array);
                    for (LngLatAlt item : list) {
                        List<Double> tem = new ArrayList<>();
                        tem.add(item.getLatitude());
                        tem.add(item.getLongitude());
                        array.add(tem);
                    }
                }
                cPolygon.setCoords(coords);
                count++;
                String line = new Gson().toJson(cPolygon);
                System.out.println("C_Polygon:\n" + line);
                saveToFile(line, cPolygon.getName());
            }

            List<List<Double>> track_coords = new ArrayList<>();
            for (LineString point : track) {
                for (LngLatAlt item : point.getCoordinates()) {
                    List<Double> tem = new ArrayList<>();
                    tem.add(item.getLatitude());
                    tem.add(item.getLongitude());
                    tem.add(34.0);
                    tem.add(55993.0);
                    tem.add(55993.0);
                    track_coords.add(tem);
                }
                String line = new Gson().toJson(track_coords);
                System.out.println("C_Track:\n" + line);
                saveToFile(line, "track");
            }

            List<ResponseTrack> responseData = new ArrayList<>();
            long maxTime = 300000;
            long maxDistance = 500000;
            for (List dot : track_coords) {
                ResponseTrack point = new ResponseTrack();
                point.setLat((Double) dot.get(0));
                point.setLon((Double) dot.get(1));
                point.setSumTime((long) (Math.random() * maxTime));
                point.setSumDistance((long) (Math.random() * maxTime));
                responseData.add(point);
            }
//            responseData.setFeatures();
            return ResponseEntity.ok(responseData);
        }
    }

    private void saveToFile(String data, String name) {
        File file = new File(name + ".txt");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileWriter writer = new FileWriter(file, false)) {
            // запись всей строки
            writer.write(data);
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }


    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Server is work");
    }
}
