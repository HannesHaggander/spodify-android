# Spodify 
Podcast focused application for Android, using podcasts found in Spotify.

## Spotify already has a podcast tab, why are you doing this? 
Personally, I like applications to have one purpose. I use Spotify for music, and I would like a separate applications for podcasts from Spotify so I decided to develop it myself.

## The project doesn't compile 
Most likely because the project is missing the secret.xml file containing the following resources: 
```
spotify_client_id
spotify_secret_id, 
spodify_redirect_url 
preference_file_key
```
Feel free to create the string resources with your Spotify project's credentials.
