# Harmonize [![Build Status](https://travis-ci.com/danielpersonius/Harmonize.svg?branch=master)](https://travis-ci.com/danielpersonius/Harmonize)
Final project for CSCI448. A customizable music recommendation system.

## TODO
- nosql db for storing client access token
- secure spotify client id, redirect uri using Android Keystore
- spotify audio feature info dialog box on song characteristics page
- seed recemmendation with all artists, tracks, and genres, not just 5
- variable feature parameter buffer
- add buffer to exported playlist description
- let user delete tracks from suggested playlists before export 
- logout from/deauthorize Spotify 
- paginate playlists and tracks with recursive getters

### layouts
- change song characteristics view to a popup/dialog box

### future possibilities
- blacklist artists, genres and tracks
- compare full suggested playlists to tracks kept by user and build a recemmendation model from that
- display that model to the user and allow for modifications and Erasure
- optionally seed from user's recent history
- long press suggested track to add to seed playlist
