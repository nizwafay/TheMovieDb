# TheMovieDb

## API token

For local development, add your TMDB read access token to `local.properties`:

```properties
tmdb.accessToken=YOUR_TMDB_READ_ACCESS_TOKEN
```

Use the TMDB API Read Access Token for Bearer auth, not the shorter legacy API key. You can also set `TMDB_ACCESS_TOKEN` in your shell environment. Do not commit API tokens. For production apps, prefer calling TMDB from your own backend so the token is not shipped inside the APK.
