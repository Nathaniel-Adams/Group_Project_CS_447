How to Run:
-Import project into eclipse/intellij dependencies should be included so it should just run.
-Run LooterGame.java as main.

Controls:
-Space bar to start game, launch game when hosting, or jump
-W,A,S,D for Movement
-Aim with mouse
-Right Click and drag mouse to rotate camera
-Scroll up/down to zoom camera
-Left Click to fire
-Escape returns to previous state
-press 1 for pistol
-press 2 for Rifle
-press 3 for Shotgun
-P spawn player
-O spawn enemy
-tab for debug

Current Issues:
-Game is in unfinished state, trying different modes such as map editing then loading single player will require reboot to function properly
-Maps do not save properly within the map editor as of issue7 merge
-LAN connections will require the GameClient ip to be changed in code to the hosts ip, which is displayed on the host's lobby state, for local connections the ip being connected to needs to be changed to "my_ip" as the argument to the socket
-Server/Client connections will connect, but the UpdateManager does not send DataPackets in a sufficient way to instantiate a game

Low Bars:
Randomized loot/Weapons-Semi-Implimented, the foundations of different stats but you can not pick drop or pickup weapons.
Randomized Dungeons-Not implimented
At least 2 unique bosses- Not implimented
4 player LAN multiplayer- Semi-Implimented, you can create a server and connect with clients but when the game starts the server seems to only send packets to the host and all the rest of the clients drop connection.
Isometric View-Implimented
Persistent/Interactive inventories-Not Implimented

Extra content:
Map Editor-Allows the user to create a dynamic map with varying tiles and allows the user to save, load and test the map.