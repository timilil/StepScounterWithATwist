# StepCounter

Project done with Android Studio and Kotlin. Utilizes Google ARCore and osmdroid (OpenStreetMap) map.
<br>
Application counts daily steps with Android internal sensor (STEP_DETECTOR) and saves them to SQLite DB.
<br>
Possibility to collect trophies which are placed to random location on the map based on the user location. If the user location to the trophy is more than 8km, the trophies are replaced. Trophy locations are also saved to SQLite.
<br>
Navigation is done with fragments (home fragment, statistics fragment, map fragment, ar fragment and settings fragment).
<br>
Home fragment shows a progress bar with steps for the day and walked high score and the day it was walked if there is one. Progress bar status is based on preference settings value (from 5000 to 15000).
<br>
<br>
Picture 1: Home view with progress bar. Preference is set to 5000 step goal for the day.
<br>
<img src="http://users.metropolia.fi/~timili/images/Screenshot_20181008-091807.png" height="440" width="240"></img>
<br>
<br>
Picture 2: Home view with progress bar. Preference is set to 10000 step goal for the day.
<br>
<img src="http://users.metropolia.fi/~timili/images/Screenshot_20181008-092040.png" height="440" width="240"></img>
<br>
<br>
Statistics fragment shows the user's all time walked steps, level, experience and trophy count as live data. Also maximum 30 days step count history is shown in a graph. Level is based on the walked steps and trophies collected. 
<br>
<br>
Picture 3: Statistics view.
<br>
<img src="http://users.metropolia.fi/~timili/images/Screenshot_20181008-091935.png" height="440" width="240"></img>
<br>
<br>
Map view (osmdroid) shows the user's location and the trophies that are able to be collected when the user is near the trophy location (less than 150 meters) and clicking it will open AR view (Google ARCore) and show the 3D trophy. One trophy will give 500 experience points and collecting them will level up faster because we want to motivate the user to walk more.
<br>
<br>
Picture 3: Map view with trophies placed to random locations (total trophy count at all times is 11). Zooming out will show more trophies on the map.<br>
<img src="http://users.metropolia.fi/~timili/images/Screenshot_20181008-091858.png" height="440" width="240"></img>
<br>
<br>
Picture 4: AR view with the 3D trophy model. Clicking it will "collect" it and give 500 experience points.
<br>
<img src="http://users.metropolia.fi/~timili/images/Screenshot_20181008-092003.png" height="440" width="240"></img>
<br>
<br>
Currently there are 4 available color themes: black, blue, green, red which are changed from the settings. Also the user can change the daily step goal from settings. This step goal value is then updated to progress bar on home view (pictures 1 and 2).
<br>
<br>
Picture 5: Settings view where the user can change step goal for the day and/or application color theme.
<br>
<img src="http://users.metropolia.fi/~timili/images/Screenshot_20181008-092027.png" height="440" width="240"></img>
<img src="http://users.metropolia.fi/~timili/images/Screenshot_20181008-092031.png" height="440" width="240"></img>
<br>
<br>
Picture 6: Application color theme changed to blue.
<br>
<img src="http://users.metropolia.fi/~timili/images/Screenshot_20181008-092027.png" height="440" width="240"></img>
<br>
<br>
Team Team
