# Spring Boot Statemachine

![Java CI with Maven](https://github.com/gcalsolaro/spring-boot-state-machine/workflows/Java%20CI%20with%20Maven/badge.svg)
> **Sample application using Statemachine powered by Spring**


## Table of Contents

   * [Spring Boot Statemachine](#spring-boot-statemachine)
      * [Architecture](#architecture)
      * [Prerequisites](#prerequisites)
      * [Rest API](#rest-api)
         * [Statemachine Rest API](#statemachine-rest-api)
      

## Architecture

Microservice architectural style is an approach to developing a single application as a suite of small services.
In this example we use spring statemachine to manage distributed transactions (two phase commit) by implementing the logic of the SAGA Pattern.
The technology stack used is provided by Spring, in particular:

* **_Spring Boot_** - 2.0.0.RELEASE
* **_Spring State Machine Core_** - 1.2.3.RELEASE
* **_Spring Data JPA with Hibernate_** - 2.0.0.RELEASE
* **_H2 Database Engine_** - 1.4.196

## Prerequisites
* **_JDK 8_** - Install JDK 1.8 version
* **_Maven_** - Download latest version



## Rest API

### Statemachine Rest API

Method | URI | Description | Parameters |
--- | --- | --- | --- |
`GET` | */api/statemachine/start/ok* | SAGA Pattern end with Istanza CREATED
`GET` | */api/statemachine/start/ko* | SAGA Pattern end with Istanza REJECTED
