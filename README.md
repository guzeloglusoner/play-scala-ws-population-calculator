# play-scala-ws-population-calculator

The aim of the project is to provide the number of people living on a specified area.

## Getting Started

To build such system, Play Framework 2.6 is used. By the help of native async. logic of Play, concurrency and scalability aimed.

### Prerequisites

To run the program, a sbt shell is needed. Intellij has all premise installation requirements.

### Logic

1. Find latitude and longitude of the city given.
2. Find nearby places with coordinates found
3. Find population field for each nearby places

### Sample Call

Request
```
localhost:9000/geo?city=london&radius=10
```

Response
```
{"totalNumberOfPeople":7557901}
```

## Built With

* [SBT](https://maven.apache.org/) - Dependency Management


## Versioning

v1.1

## Authors

* **Soner Guzeloglu** - *Initial work*



