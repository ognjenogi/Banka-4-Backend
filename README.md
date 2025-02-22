# Banka 4 — worlds greatest bank

# Development
## Using the development compose file
To start up the backend and its services, execute:

```
docker compose up --build --watch
```

… in the root directory of the repository.  Upon doing so, Docker will build
each service and start them up with watch enabled.  This means that every time
you edit the source code of any service, it will get automatically rebuilt and
restarted.  This rebuild and restart usually takes somewhere in the range of 6
to 10 seconds.

By convention, for each of the services, the `dev` profile is activated.  This
means that one can store development-specific properties in
`application-dev.EXT`, where `EXT` is `yml` or `properties`.
