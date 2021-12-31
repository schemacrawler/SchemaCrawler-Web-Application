# SchemaCrawler Web Application API on RapidAPI

Create a SchemaCrawler schema diagram from an uploaded SQLite database file


## Create a SchemaCrawler schema diagram

Have a SQLite database file on your local directory, called "test.db". Use a request similar to the one below to generate a diagram. Substitute the RapidAPI key. Capture the SchemaCrawler diagram key from the JSON response.

```sh
curl -H "X-RapidAPI-Key: <<key>>" -H "X-RapidAPI-Host: schemacrawler-web-application1.p.rapidapi.com" -H "Accept: application/json" -F "name=Sualeh Fatehi" -F "email=sualeh@hotmail.com" -F "file=@test.db" https://schemacrawler-web-application1.p.rapidapi.com/diagrams
```


## Retrieve a SchemaCrawler schema diagram request

Obtain the original diagram request. Substitute the RapidAPI key and the SchemaCrawler diagram key.

```sh
curl -H "X-RapidAPI-Key: <<key>>" -H "X-RapidAPI-Host: schemacrawler-web-application1.p.rapidapi.com" -H "Accept: application/json"  https://schemacrawler-web-application1.p.rapidapi.com/diagrams/<<diagram-key>>
```


## Retrieve a SchemaCrawler schema diagram

Obtain the generated diagram. Substitute the RapidAPI key and the SchemaCrawler diagram key.

```sh
curl -H "X-RapidAPI-Key: <<key>>" -H "X-RapidAPI-Host: schemacrawler-web-application1.p.rapidapi.com" -H "Accept: image/png"  https://schemacrawler-web-application1.p.rapidapi.com/diagrams/<<diagram-key>>/diagram --output diagram.png
```
