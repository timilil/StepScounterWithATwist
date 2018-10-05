# StepCounter

Project done with Android Studio and Kotlin.
<br>
Application counts daily steps with Android internal sensor (STEP_DETECTOR), saves them to SQLite DB.
Possibility to collect trophies which are spawned on the map based on the user location. If the location changes more than 8km, the trophies are respawned. Trophy locations are also saved to SQLite.
Navigation is done with fragments (home fragment, statistics fragment, map fragment, ar fragment and settings fragment)
Home fragment shows a progress bar with steps for the day and walked high score and the day it was walked if there is one. Progress bar status is based on preference settings value (from 5000 to 15000).
Statistics fragment shows the user's all time walked steps, level, experience and trophy count. Level is based on the walked steps and trophies collected. 
Map view shows the trophies that are able to be collected when the user is near the trophy location and clicking it will open AR view and show the 3D trophy. One trophy will give 500+ points and collecting them will level up faster because we want to motivate the user to walk more.
Currently there are 4 available themes: black, blue, green, red which are changed from the settings. Also the user can change the daily step goal from settings. This step goal value then is updated to progress bar on home view.

Team Team: Joni Tefke, Timi Liljeström
