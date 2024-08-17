## Twitch Bot Tool

For my own personal use on twitch, but the code is public in case
anyone else needs examples of how to use twitch4j and the obs 
websocket library together to make things happen like:

- Use implicit client credential flow for twitch auth
- display chat in a transparent window via Swing
- cause reward redemptions to trigger actions in OBS
- Hide scene items in OBS
- Change file path sources for image scene items in OBS

Most likely if you're reading this you won't be able to run
this code with OBS and your own stream without modifying the
code first, but it should provide some basic examples and be a
useful reference should you need it!

### Things of note

_For future me, and for you_

- InputSettings is how you change a file path for a scene item,
there's no setSceneItemX type thing. 
- Responses from OBS return null message datas if it doesn't exist
in the current scene being displayed.
- To get the name of the current scene, call the list endpoint and
grab the property for the program window from that.
- You have to enable the websocket plugin in OBS in settings!

### Running

Pass the arguments in:

1. clientId.txt: make this file and put your twitch bot id in
2. clientsecret.txt make this file and put your twitch bot secret in
3. channel: 3rd argument should the channel name to connect for chat
4. IP: the ip address of your obs instance running the websocket plugin
5. password: the obs password used to connect to obs