# ReRollBag Server

The "ReRollBag" project is an Android application aimed at promoting sustainable consumption by encouraging the use of
reusable bags instead of plastic bags.

This readme.md is for the backend server application for the ReRollBag project. Also, this is for **Google Solution
Challenge**, topics with **Responsible Consumption & Production.**

The server is developed using SpringBoot with Java and incorporates various technologies such as Test Driven
Development, Spring REST Docs, Spring Data Jpa, Spring Security, JUnit5, AssertJ, and databases including RDBMS (MySQL,
H2) and NoSQL (Redis, MongoDB). This application is also hosted on the Google Cloud Platform.

---

## Features

* `Custom login` and `social media account login` for "ReRollBag"
* `Renting` of reusable bags `through QR codes` attached to the bags
* `Returning` the rented bags to designated locations `using QR codes` before the one-week rental period ends
* `Promoting the use of reusable bags` by allowing users to borrow and return bags without a deposit for rental

---

## Library & Frameworks

The following Library and Frameworks were used in the development of this project:

* `SpringBoot with Java`, `Spring Data Jpa`, `Spring Security`
* `Test Driven Development`, `JUnit5`, `AssertJ`, `Spring REST Docs`
* `RDBMS (MySQL, H2)`, `NoSQL (Redis, MongoDB)`
* `Google Cloud Platform Computing Engine`

---

## Getting Started

### Constraints

Because it's the backend server project which is working now, **so it is hard to open all the properties files which
contain key value.** So, If you want to clone this projects, you have to set properties like below. If you just want to
check how does our server works, you can see **Usage** and **API specification** below.

### Prerequisites

* Java 8 or higher
* MySQL or H2 database
* Redis and MongoDB

### Installation

* Clone the repository to your local machine
* Navigate to the root directory of the project
* Make 5 .properties files at src/main/resources and implement in your local environment :
    * application.properties
  ```
  spring.h2.console.enabled=true
  spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
  spring.datasource.url=jdbc:h2:mem:[Your Database]
  spring.datasource.driverClassName=org.h2.Driver
  spring.datasource.username=[Your username]
  spring.datasource.password=[Your password]
  ```
    * location.properties
  ```
  location.firebaseKey = src/main/resources/keys/Firebase-Admin-SDK-Key.json
  ```
    * mongo.properties
  ```
  spring.data.mongodb.host=localhost
  spring.data.mongodb.port=[Your Port]
  spring.data.mongodb.database=[Your Database]
  spring.data.mongodb.username=[Your Username]
  spring.data.mongodb.password=[Your password]

  spring.data.mongodb.uri=mongodb://localhost:[Your port]/[Your Database]
  ```
    * redis.properties
  ```
  spring.redis.host=localhost
  spring.redis.port=6379
  ```
    * security.properties
  ```
  jwtToken.secretKey = [Your Secret Key]
  ```
* Make 1 .json files at src/main/resources/keys and implement in your local environment :
    * Firebase-Admin-SDK-Key.json
  ```
  {
    "type": ,
    "project_id": ,
    "private_key_id": ,
    "private_key": ,
    "client_email": ,
    "client_id": ,
    "auth_uri": ,
    "token_uri": ,
    "client_x509_cert_url": 
  }
  ```

---

## Usage

The server provides APIs for managing the rental and return of reusable shopping bags using QR codes. The APIs can be
accessed using a client application that supports HTTP requests.

For detailed information on how to use the APIs, please refer to the API documentation generated by Spring REST Docs.

| Domain       | URL                                    |
|--------------|----------------------------------------|
| Users        | http://34.64.247.152:8080/docs/users   |
| Token (Auth) | http://34.64.247.152:8080/docs/auth    |
| Bags         | http://34.64.247.152:8080/docs/bags    |
|Marker| http://34.64.247.152:8080/docs/markers |
|Notice| http://34.64.247.152:8080/docs/notices |

## Testing

The project was developed using Test Driven Development, and the tests are located in the src/test/java directory.

To run the tests, navigate to the root directory of the project and run `./gradlew test`
