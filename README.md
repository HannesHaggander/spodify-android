# Spodify 
Podcast only focused app for Android retrieving podcasts from the Spotify backend

## Spotify already has a podcast tab, why are you doing this? 
I like to have my application functionality separated. I want to use Spotify for music and music only and use another application entirely for podcasts.

## The project doesn't compile 
Most likely becuase the project is missing the secret.xml file containing the following resources: 
```
spotify_client_id
spotify_secret_id, 
spodify_redirect_url 
preference_file_key
```
Feel free to create the string resources with your spotify project's credentials.
