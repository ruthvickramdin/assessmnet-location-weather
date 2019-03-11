/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.walmartassessmenttest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Walmart Assessment - Question #2 (Back-End Role)
 * @author ruthvickramdin
 */
public class WalmartAssessment {

    public static ArrayList<Location> locationList;
    
    /**
     * Route types
     */
    public enum routeType {
        
        Fastest("Fastest"),
        Shortest("Shortest");        
     
        private final String value;
        
        private routeType(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return this.value;
        }
    }
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.net.URISyntaxException
     */
    public static void main(String[] args) throws IOException, URISyntaxException {
        
        try {
            
            locationList = Location.getAllLocations();
            System.out.println("---------------------------------------------------------------------------------------------------");
            System.out.println("\t\t\tINPUT");
            System.out.println("---------------------------------------------------------------------------------------------------");
            for(Location loc : locationList) {
                System.out.println(loc.getStoreNbr() + "\t\t" + loc.getAddress()+ "\t\t" + loc.getCity()+ "\t\t" + loc.getState());
            }
            System.out.println();
            System.out.println("---------------------------------------------------------------------------------------------------");
            System.out.println("\t\t\tOUTPUT");
            System.out.println("---------------------------------------------------------------------------------------------------");
            Route closeStoreByDistance = getClosestStoreByDistance();
            if(closeStoreByDistance != null) {
                System.out.println("Closest store by distance: " + closeStoreByDistance.storeNumber);
                //Optional statement
                //System.out.println("Located on " + closeByDistance.destinationAddress + " with travel distance of " + closeByDistance.distance + " and travel time of " + closeByDistance.travelTime);
            }

            Route closeStoreByDrive = getClosestStoreByDriveTime(); 
            if(closeStoreByDrive != null) {
                System.out.println("Closest store by drive time: " + closeStoreByDrive.storeNumber );
                //Optional statement
                //System.out.println("Located on " + closeByDrive.destinationAddress + " with travel distance of " + closeByDrive.distance + " and drive time of " + closeByDrive.travelTime);
            }
            
            Location homeOffice = Location.getHomeOfficeLocation();
            Weather homeOfficeWeather = getWeather(homeOffice.city, homeOffice.state);
            if(homeOfficeWeather != null) {
                System.out.println("Weather for Walmart HomeOffice on " + LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/YYYY")) + ": " + homeOfficeWeather.currentTemp + "F and " + homeOfficeWeather.description);
            }
            
            //Assumption - Closest store based on shortest distance
            Location location = Location.getLocationDetails(closeStoreByDistance.storeNumber);
            Weather closestStoreWeather = getWeather(location.city, location.state);
            if(closestStoreWeather != null) {
                System.out.println("Weather for closest store on " + LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/YYYY")) + ": " + closestStoreWeather.currentTemp + "F and " + closestStoreWeather.description);
            }
            
            String storeNumber = getLocationWithNicestWeather();
            if(!storeNumber.isEmpty()) {
                System.out.println("Location with the nicest weather: " + storeNumber);
            }
        }
        catch (IOException | URISyntaxException e) {
            throw e;
        }
    }
    
    /**
     * 
     * @return
     * @throws IOException
     * @throws URISyntaxException 
     */
    public static Route getClosestStoreByDistance() throws IOException, URISyntaxException {
        
        ArrayList<Location> locationList = Location.getAllLocations(); 
        return Route.getShortestRoute(locationList, routeType.Shortest);
    }

    /**
     * 
     * @return
     * @throws URISyntaxException
     * @throws IOException 
     */
    public static Route getClosestStoreByDriveTime() throws URISyntaxException, IOException {

        ArrayList<Location> locationList = Location.getAllLocations(); 
        return Route.getFastestRoute(locationList, routeType.Fastest);
    }
     
    /**
     * 
     * @return 
     * @throws java.io.IOException 
     * @throws java.net.URISyntaxException 
     */
    public static Weather getWeather(String city, String state) throws IOException, URISyntaxException {
            
        return Weather.getCurrentWeather(city, state);
    }
    
    /**
     * 
     * @return 
     * @throws java.io.IOException 
     * @throws java.net.URISyntaxException 
     */
    public static String getLocationWithNicestWeather() throws IOException, URISyntaxException {
            
        ArrayList<Location> locationList = Location.getAllLocations(); 
        return Weather.getNicestWeather(locationList);
    }
    
    /**
     * 
     */
    static class Location {
        
        String storeNbr;
        String address;
        String city;
        String state;
        
        Location(String sNbr, String addr, String cty, String sta) {
            storeNbr = sNbr;
            address = addr;
            city = cty;
            state = sta;
        }

        public void setStoreNbr(String storeNbr) {
            this.storeNbr = storeNbr;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getStoreNbr() {
            return storeNbr;
        }

        public String getAddress() {
            return address;
        }

        public String getCity() {
            return city;
        }

        public String getState() {
            return state;
        }
        
        /**
         * 
         * @param storeNbr
         * @return 
         */
        private static Location getLocationDetails(String storeNbr) {
            
            ArrayList<Location> locList = getAllLocations();
            
            for(Location loc : locList) {
                if(loc.storeNbr.equals(storeNbr))
                    return loc;
            }
            return null;
        }
        
        /**
         * Included input location data directly in the code due to time constraint
         * This info can be read, parsed and loaded from an external file as well
         * @return 
         */
        private static ArrayList<Location> getAllLocations() {
         
            locationList = new ArrayList<>();
            
            locationList.add(new Location("HomeOffice", "702 SW 8th St", "Bentonville", "Arkansas"));
            locationList.add(new Location("5402", "4650 W North Ave", "Chicago", "Illinois"));
            locationList.add(new Location("772", "3506 Highway 6 S", "Houston", "Texas"));
            locationList.add(new Location("1549", "2020 N 75th Ave", "Phoenix", "Arizona"));
            locationList.add(new Location("2141", "1675 Christopher Columbus Blvd", "Philadelphia", "Pennsylvania"));
            locationList.add(new Location("2150", "710 Dennery Road", "San Diego", "California"));
            locationList.add(new Location("1082", "10991 San Jose Blvd Ste 1", "Jacksonville", "Florida"));
            
            return locationList;
        }
        
        /**
         * 
         * @return 
         */
        private static Location getHomeOfficeLocation() {
            
            String homeOfficeStoreNumber = "HomeOffice";
            return getLocationDetails(homeOfficeStoreNumber);
            
        }
        
        /**
         * 
         * @return 
         */
        private String getFullAddress() {
            return (address + ", " + city + ", " + state);
        }
    }
    
    /**
     * 
     */    
    static class Route {
        
        private static final String API_KEY = "U05s3uYGkUq0v8lXQXacZuPGLMPOQWP5";
        private static final String SCHEME = "http";
        private static final String HOST = "www.mapquestapi.com";
        private static final String PATH = "/directions/v2/route";

        String storeNumber;
        String sourceAddress;
        String destinationAddress;
        Double distance;
        String timeInSeconds;
        String travelTime;
        String routeType;
        
        Route(String _storeNumber, String _sourceAddr, String _destAddr, Double _distance, String _timeInSeconds, String _travelTime, String _routeType) {
            storeNumber = _storeNumber;
            sourceAddress = _sourceAddr;
            destinationAddress = _destAddr;
            distance = _distance;
            timeInSeconds = _timeInSeconds;
            travelTime = _travelTime;
            routeType = _routeType;
        }
        
        /**
         * 
         * @param storeNum
         * @param sourceAddress
         * @param destinationAddress
         * @param type
         * @return
         * @throws IOException
         * @throws URISyntaxException 
         */
        private static Route getRoute(String storeNum, String sourceAddress, String destinationAddress, routeType type) throws IOException, URISyntaxException {
            
            return getRouteFromAPI(storeNum, sourceAddress, destinationAddress, type);
        }
        
        /**
         * 
         * @param locationList
         * @param type
         * @return
         * @throws IOException
         * @throws URISyntaxException 
         */
        private static Route getShortestRoute(ArrayList<Location> locationList, routeType type) throws IOException, URISyntaxException {
            
            TreeMap<Double, Route> treeMap = new TreeMap<>();
        
            Location homeOfficeLocation = Location.getHomeOfficeLocation();
            String homeOfficeFullAddress = homeOfficeLocation.getFullAddress();

            for(Location loc : locationList) {

                String destinationAddress = loc.getFullAddress();
                if(!homeOfficeFullAddress.equals(destinationAddress) && !homeOfficeLocation.storeNbr.equals(loc.storeNbr)) {

                    Route op = getRoute(loc.storeNbr, homeOfficeFullAddress, loc.getFullAddress(), type);

                    if(!treeMap.containsKey(op.distance)) 
                        treeMap.put(op.distance, op);
                }
            }

            return (treeMap.size() > 0) ? treeMap.firstEntry().getValue() : null;
        }
        
        /**
         * 
         * @param locationList
         * @param type
         * @return
         * @throws URISyntaxException
         * @throws IOException 
         */
        private static Route getFastestRoute(ArrayList<Location> locationList, routeType type) throws URISyntaxException, IOException {
            
            TreeMap<String, Route> treeMap = new TreeMap<>();
        
            Location homeOfficeLocation = Location.getHomeOfficeLocation();
            String homeOfficeFullAddress = homeOfficeLocation.getFullAddress();

            for(Location loc : locationList) {

                String destinationAddress = loc.getFullAddress();
                if(!homeOfficeFullAddress.equals(destinationAddress) && !homeOfficeLocation.storeNbr.equals(loc.storeNbr)) {

                    Route op = Route.getRoute(loc.storeNbr, homeOfficeFullAddress, loc.getFullAddress(), type);

                    if(!treeMap.containsKey(op.timeInSeconds)) 
                        treeMap.put(op.timeInSeconds, op);
                }
            }

            return (treeMap.size() > 0) ? treeMap.firstEntry().getValue() : null;
        }
        
        /**
         * Make call to the MapQuest API in here
         * @param storeNum
         * @param sourceAddress
         * @param destinationAddress
         * @param type
         * @return
         * @throws IOException
         * @throws URISyntaxException 
         */
        private static Route getRouteFromAPI(String storeNum, String sourceAddress, String destinationAddress, routeType type) throws IOException, URISyntaxException {
            
            try {
                
                BufferedReader in = null;
                StringBuilder strBuilder;
                String readLine = null;
                
                URIBuilder uri = new URIBuilder();
                uri.setScheme(SCHEME);
                uri.setHost(HOST);
                uri.setPath(PATH);
                uri.addParameter("key", API_KEY);
                uri.addParameter("from", sourceAddress);
                uri.addParameter("to", destinationAddress);
                uri.addParameter("outFormat", "json");
                uri.addParameter("routeType", type.getValue());

                URL url = uri.build().toURL();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(15*1000);
                conn.connect();
                
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                strBuilder = new StringBuilder();
                
                while ((readLine = in .readLine()) != null) {
                    strBuilder.append(readLine);
                } in .close();
                
                JSONObject obj = CreateRouteObject(strBuilder.toString());
                
                Route rObj = new Route(storeNum, sourceAddress, destinationAddress, Double.parseDouble(obj.get("distance").toString()), 
                        obj.get("timeInSeconds").toString(), obj.get("travelTime").toString(), type.getValue());
                
                return rObj;
            }
            catch (IOException | URISyntaxException e) {
                throw e;
            }
        } 
        
        /**
         * Parse Json string to Json Object
         * @param strInput
         * @return 
         */
        private static JSONObject CreateRouteObject(String strInput) {

            if(!strInput.isEmpty()) {
             
                JSONObject obj = new JSONObject(strInput).getJSONObject("route");
                 
                JSONObject newObj = new JSONObject();
                
                newObj.put("distance", obj.get("distance"));
                newObj.put("timeInSeconds", obj.get("time"));
                newObj.put("travelTime", obj.get("formattedTime"));
                
                return newObj;
                
            }
            return null;
        }
    }
    
    /**
     * 
     */
    static class Weather {
        
        private static final String APPID = "932160016db143de171ade814197fa98";
        private static final String SCHEME = "http";
        private static final String HOST = "api.openweathermap.org";
        private static final String PATH = "/data/2.5/weather";

        Double currentTemp;
        Double maxTemp;
        Double minTemp;
        String windSpeed;
        String humidty;
        String clouds;
        String description;
        String weatherGroup;
        
        Weather(Double _currentTemp, Double _maxTemp, Double _minTemp, String _windSpeed, String _humidty, String _clouds, String _description, String _weatherGroup) {
            currentTemp = _currentTemp;
            maxTemp = _maxTemp;
            minTemp = _minTemp;
            windSpeed = _windSpeed;
            humidty = _humidty;
            clouds = _clouds;
            description = _description;
            weatherGroup = _weatherGroup;
        }
        
        /**
         * 
         * @param cityName
         * @param stateName
         * @return
         * @throws IOException
         * @throws URISyntaxException 
         */
        private static Weather getCurrentWeather(String cityName, String stateName) throws IOException, URISyntaxException {
            
            return getWeatherFromAPI(cityName, stateName);
        }
        
        /**
         * 
         * @param locationList
         * @return
         * @throws IOException
         * @throws URISyntaxException 
         */
        private static String getNicestWeather(ArrayList<Location> locationList) throws IOException, URISyntaxException {
            
            TreeMap<String, Weather> map = new TreeMap<>();
            
            for(Location loc : locationList) {
                
                Weather wObj = getCurrentWeather(loc.city, loc.state);
                map.put(loc.storeNbr, wObj);    
            }
            
            String storeNumber = getWeatherPrediction(map);
            
            return storeNumber;
        }
        
        /**
         * 
         * @param map
         * @return 
         */
        private static String getWeatherPrediction(TreeMap<String, Weather> map) {
             
            Entry<String, Weather> first = map.firstEntry();
            Entry<String, Weather> last = map.lastEntry();
            
            //Sunny temperature
            if((first.getValue().currentTemp >= 60 && first.getValue().currentTemp < 80) 
                    && (last.getValue().currentTemp >= 60 && last.getValue().currentTemp < 80))
                return map.keySet().toArray()[map.size() / 2].toString();
               
            //Little Cold temperature
            else if ((first.getValue().currentTemp >= 30 && first.getValue().currentTemp < 60)
                && (last.getValue().currentTemp >= 30 && last.getValue().currentTemp < 60))
                return map.keySet().toArray()[map.size() - 1].toString();
            
            //Very Sunny temperature
            else if (first.getValue().currentTemp > 80 && last.getValue().currentTemp > 80)
                return map.keySet().toArray()[0].toString();
            
            //Very Cold temperature
            else if (first.getValue().currentTemp < 30 && last.getValue().currentTemp < 30)
                return map.keySet().toArray()[map.size() - 1].toString();
            
            //default tempearature from the map
            else
                return map.keySet().toArray()[0].toString();
        }
        
        /**
         * This method is a sample method that defines a subset of the whole weather prediction algorithm.
         * Due to lack of time I couldn't implement it completely
         * Method not used anywhere
         * @param map
         * @return 
         */
        private static String getWeatherPredictionExtension(HashMap<String, Weather> map) {
            
            Double niceTemp = 0.0;
            String niceWeatherStore = null;
            
            for (Map.Entry<String, Weather> entry : map.entrySet()) {
                
                Double currentTemp = entry.getValue().currentTemp;
                String windSpeed = entry.getValue().windSpeed;
                String weatherGroup = entry.getValue().weatherGroup;
                String clouds = entry.getValue().clouds;
                
                if(currentTemp >= 65 && currentTemp <= 80 && Integer.parseInt(windSpeed) < 5 
                        && weatherGroup.equalsIgnoreCase("Clear") && Double.parseDouble(clouds) < 1) {
                    
                }
                else if(currentTemp >= 40 && currentTemp < 65 && Integer.parseInt(windSpeed) < 5 
                        && weatherGroup.equalsIgnoreCase("Clear") && Double.parseDouble(clouds) < 1) {
                    
                }
                else if(currentTemp > 80 && Integer.parseInt(windSpeed) < 5 
                        && weatherGroup.equalsIgnoreCase("Clear") && Double.parseDouble(clouds) < 1) {
                    
                } 
                else if(currentTemp >= 65 && currentTemp <= 80 && Integer.parseInt(windSpeed) < 5 
                        && weatherGroup.equalsIgnoreCase("Rainy") && Double.parseDouble(clouds) > 1) {
                    
                }
                else if(currentTemp >= 40 && currentTemp < 65 && Integer.parseInt(windSpeed) < 5 
                        && weatherGroup.equalsIgnoreCase("Rainy") && Double.parseDouble(clouds) > 1) {
                    
                }
                else if(currentTemp > 80 && Integer.parseInt(windSpeed) < 5 
                        && weatherGroup.equalsIgnoreCase("Rainy") && Double.parseDouble(clouds) > 1) {
                    
                } 
                else if(currentTemp > 40 && Integer.parseInt(windSpeed) < 5 
                        && weatherGroup.equalsIgnoreCase("Snowy") && Double.parseDouble(clouds) > 1) {
                    
                }
                else if(currentTemp > 40 && Integer.parseInt(windSpeed) < 5 
                        && weatherGroup.equalsIgnoreCase("Snowy") && Double.parseDouble(clouds) > 1) {
                    
                }
                else if(currentTemp > 40 && Integer.parseInt(windSpeed) < 5 
                        && weatherGroup.equalsIgnoreCase("Snowy") && Double.parseDouble(clouds) > 1) {
                    
                }    
            }
            
            return niceWeatherStore;
        } 
        
        /**
         * Make call to the OpenWeatherMap API in here
         * @param cityName
         * @param stateName
         * @return
         * @throws IOException
         * @throws URISyntaxException 
         */
        private static Weather getWeatherFromAPI(String cityName, String stateName) throws IOException, URISyntaxException {
            
            try {
                
                BufferedReader in = null;
                StringBuilder strBuilder;
                String readLine = null;
                
                URIBuilder uri = new URIBuilder();
                uri.setScheme(SCHEME);
                uri.setHost(HOST);
                uri.setPath(PATH);
                uri.addParameter("q", (cityName + "," + stateName));
                uri.addParameter("units", "imperial");
                uri.addParameter("lang", "en");
                uri.addParameter("APPID", APPID);
                URL url = uri.build().toURL();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(15*1000);
                conn.connect();
                
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                strBuilder = new StringBuilder();
                
                while ((readLine = in .readLine()) != null) {
                    strBuilder.append(readLine);
                } in .close();
                
                JSONObject obj = CreateRouteObject(strBuilder.toString());
                
                Weather wObj = new Weather(Double.parseDouble(obj.get("currentTemp").toString()), Double.parseDouble(obj.get("maxTemp").toString()), 
                        Double.parseDouble(obj.get("minTemp").toString()), obj.get("windSpeed").toString(), obj.get("humidty").toString(), 
                        obj.get("clouds").toString(), obj.get("description").toString(), obj.get("weatherGroup").toString());
                
                return wObj;
            }
            catch (IOException | URISyntaxException e) {
                throw e;
            }
        } 
        
        /**
         * Parse Json string to Json Object
         * @param strInput
         * @return 
         */
        private static JSONObject CreateRouteObject(String strInput) {

            if(!strInput.isEmpty()) {
                   
                Object weatherObj = new JSONObject(strInput).get("weather");
                JSONArray jArr = (JSONArray)weatherObj;
                JSONObject weatherSubObj = (JSONObject)jArr.get(0);
                
                JSONObject mainSubObj = new JSONObject(strInput).getJSONObject("main");
                JSONObject windsSubObj = new JSONObject(strInput).getJSONObject("wind"); 
                JSONObject cloudsSubObj = new JSONObject(strInput).getJSONObject("clouds");
                 
                JSONObject newObj = new JSONObject();
                
                newObj.put("currentTemp", mainSubObj.get("temp"));
                newObj.put("maxTemp", mainSubObj.get("temp_max"));
                newObj.put("minTemp", mainSubObj.get("temp_min"));
                newObj.put("humidty", mainSubObj.get("humidity"));
                newObj.put("clouds", cloudsSubObj.get("all"));
                newObj.put("windSpeed", windsSubObj.get("speed"));
                newObj.put("description", weatherSubObj.get("description"));
                newObj.put("weatherGroup", weatherSubObj.get("main"));
                
                return newObj;
            }
            return null;
        }   
    } 
}