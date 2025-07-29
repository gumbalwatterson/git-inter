# Github integration API
Application used for fetching data from Github.

## General info
Application expose one endpoint to collect available non fork repositories and corresponding branches
by github user name. Response returned to the client is in JSON format. API returns to 30 results per request.
Application has defined advice controller which handle any exceptions during request processing. If requested user
is not found appropriate message is return to the client. Details exceptions messages are log using Slf4j.

Application uses Github API to collect data. Documentation for Github API can be find under that link:
https://developer.github.com/v3

### Request example
Request to endpoint returning non fork repositories
````
http://localhost:8080/api/nonfork/repos/SalithaUCSC
````
Snippet response
````
{
    "repositories": [
        {
            "owner": "SalithaUCSC",
            "repositoryName": "actuator-dashboard",
            "branches": [
                {
                    "branchName": "dashboard-v1",
                    "sha": "14305f5a57f672cb5dcd3bbf4b1a5fcc759e5a7c"
                },
                {
                    "branchName": "main",
                    "sha": "779e60003dae2d576ffd04d1f09c2f6dc36c3f9f"
                }
            ],
            "_links": {
                "self": {
                    "href": "http://localhost:8080/api/repos/user/SalithaUCSC/actuator-dashboard?page=1"
                }
            }
        },
        {
            "owner": "SalithaUCSC",
            "repositoryName": "admin-dashboard",
            "branches": [
                {
                    "branchName": "dev",
                    "sha": "c62703c493d0db1b28b78c12c5baf2c24f4356c6"
                },
                {
                    "branchName": "dev-v2",
                    "sha": "d7b29a869a1dc65b4dd6d6e909eab4868cd27279"
                },
                {
                    "branchName": "main",
                    "sha": "e51bf60e2c52cc485251fcaf59a13048eb798d89"
                }
            ],
            "_links": {
                "self": {
                    "href": "http://localhost:8080/api/repos/user/SalithaUCSC/admin-dashboard?page=1"
                }
            }
        }
    ],
    "_links": {
        "self": {
            "href": "http://localhost:8080/api/repos/nonfork/user/SalithaUCSC?page=1"
        },
        "next_page": {
            "href": "http://localhost:8080/api/repos/nonfork/user/SalithaUCSC?page=2"
        }
    }
}
````


## Technologies
Main technologies:
* Java 21
* Spring Boot 3.5.4
* Lombok 1.18.38

## Setup
Setup details:
* set up Java runtime environment locally
* start application locally using: mvn spring-boot:run (by default application runs on port 8080)
* if you have problem with 'access denied' for file logback.xml you can change local permissions or remove this file for local deployment
* Example link to test endpoint locally: http://localhost:8080/api/nonfork/repos/SalithaUCSC