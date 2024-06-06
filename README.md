# Maps Project

This project is a comprehensive implementation of Google Maps features, including finding and marking device locations, keyword location searching, autocomplete suggestions, changing map styles, and integrating various Google Maps APIs.

## Features

- **Find and Mark Device Locations**: Automatically locate and mark the user's current location on the map.
- **Keyword Location Searching and Autocomplete Suggestions**: Search for locations using keywords with real-time autocomplete suggestions.
- **Change Map Styles**: Dynamically switch between different map styles, such as day and night modes.
- **Google Maps APIs**: Integration with various Google Maps APIs for enhanced functionality.
- **Haversine Algorithm**: Utilizes the Haversine algorithm to calculate the shortest distance between points and determine the nearest station to the user's location.


## Google Maps APIs Used

1. **Maps SDK for Android**
    - **Usage**: To display the map, handle user interactions, and add markers or shapes.
    - **Example**: 
      ```kotlin
      map.addMarker(MarkerOptions().position(currentLatLng).title("You are here"))
      ```

2. **Places API**
    - **Usage**: To search for places, get place details, and provide autocomplete suggestions.
    - **Example**:
      ```kotlin
      val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
      val request = FetchPlaceRequest.newInstance(placeId, placeFields)
      placesClient.fetchPlace(request)
      ```

3. **Distance Matrix API**
    - **Usage**: To calculate the travel distance and time between multiple origins and destinations.
    - **Example**:
      ```kotlin
      val origins = "40.6655101,-73.89188969999998"
      val destinations = "40.659569,-73.933783|40.729029,-73.851524|40.6860072,-73.6334271|40.598566,-73.7527626"
      directionsService.getDistanceMatrix(origins, destinations, apiKey)
      ```

4. **Directions API**
    - **Usage**: To get route information between an origin and a destination, including step-by-step directions.
    - **Example**:
      ```kotlin
      directionsService.getDirections(origin, destination, apiKey)
      ```

5. **Routes API**
    - **Usage**: To calculate and display multiple routes from an origin to a destination.
    - **Example**:
      ```kotlin
      routeDrawingService.getRoutes(origin, destination, apiKey)
      ```


## Images
![maps1](https://github.com/Tonistark92/MapsTutorials/assets/86676102/d5246b2f-c751-48b9-8c63-33fdd297ee5f)
![maps2](https://github.com/Tonistark92/MapsTutorials/assets/86676102/25956a82-b870-4d8e-adc6-73d8f550a55c)
![maps3](https://github.com/Tonistark92/MapsTutorials/assets/86676102/0eee54e8-6604-4573-ab56-70067a4722c9)
![maps4](https://github.com/Tonistark92/MapsTutorials/assets/86676102/00d27315-b09d-4691-b7be-d7443b6b6a36)
![maps5](https://github.com/Tonistark92/MapsTutorials/assets/86676102/2c1265d2-5f31-4bee-ba45-51290ecf4551)
![maps6](https://github.com/Tonistark92/MapsTutorials/assets/86676102/ec2f478e-4cd7-42b0-8894-923b82b938b9)
![maps7](https://github.com/Tonistark92/MapsTutorials/assets/86676102/ab6e73a4-0c2f-479c-ae7a-989d53a0c494)
![maps8](https://github.com/Tonistark92/MapsTutorials/assets/86676102/00d83fa7-e7e0-49f6-a154-cc6bdef4bcdd)










## Installation

To run this project, follow these steps:

1. **Clone the Repository**:
    ```bash
    git clone https://github.com/your-username/maps-project.git
    cd maps-project
    ```

2. **Open in Android Studio**: Open the project in Android Studio.

3. **Add Google Maps API Key**:
    - Obtain an API key from the [Google Cloud Console](https://console.cloud.google.com/).
    - Add the API key to your `local.properties` file:
      ```properties
      MAPS_API_KEY=YOUR_API_KEY_HERE
      ```

4. **Add JitPack Repository** (if using libraries from JitPack):
    ```gradle
    allprojects {
        repositories {
            google()
            mavenCentral()
            maven { url "https://jitpack.io" }
        }
    }
    ```


## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
