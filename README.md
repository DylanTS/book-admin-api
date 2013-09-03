# Library API #

[![Build Status](https://api.travis-ci.org/DylanTS/library-api.png)](https://api.travis-ci.org/DylanTS/library-api.png)

Provides a REST API for a mock (book) library

## Overview ##

This project was an excuse to build a Java REST based application using Spring.  The application provides an API around
the mechanics of a library, but currently only contains the Book resource.  More to be added...

## Technical Details ##

This uses Spring's <code>AbstractAnnotationConfigDispatcherServletInitializer</code> to allow for a web.xml-less web
application project.  All configuration that would be stored within the web.xml is instead stored within the
<code>org.library.WebApplicationInitializer</code>.  Spring Beans are provided within <code>@Configuration</code>
classes instead of xml files.  The project uses Spring Profiles to dynamically load the configuration based on the
environment.  For example, in the <code>development</code> environment the
<code>org.library.config.data.DevelopmentDataConfig</code> is loaded which uses an embedded <code>DataSource</code>
and populates the database with a preconfigured set of books found in the <code>data.json</code> file.  However
in the <code>production</code> environment the <code>org.library.config.data.ProductionDataConfig</code> is loaded
which uses JNDI to retrieve the <code>DataSource</code> and does not pre-populate the database.

Spring Data is used to configure and load the database, as well as the entities and repositories.  For example,
the <code>org.library.repository.BookRepository</code> references the <code>org.library.domain.Book</code> entity.
The entities extend from an internal <code>org.library.domain.AbstractPersistable</code> which provides its own
ID generator: <code>org.library.persistence.UseExistingOrGenerateIdGenerator</code>.  This was necessary to use IDs
specified in the <code>data.json</code> data file used to pre-populate the database, but also generate unique IDs
when the ID is not specified.  The repository interfaces extend from Spring Data to automatically provide default
CRUD operations, as well as paging and sorting functions.

Validation is done at the API layer to enforce correct data on POST or PUT operations.  This uses a combination of
JSR-303 and Hibernate to provide validation.  Annotations are added to the entity class to declare the requirements
and the <code>@Valid</code> annotation is added to the bean itself in the controllers.  A <code>MessageSource</code>
is defined with error messages stored in <code>messages/errors.properties</code>.  Because the messages were not
supplied by default, an exception resolver was added to use the message in the response error message if available
(<code>org.library.config.exception.IncludeMessageSourceExceptionResolver</code>).

Both unit and integration tests were provided.  For integration tests, Spring Test was used to load up the configuration
prior to running the tests.  <code>org.springframework.test.web.servlet.MockMvc</code> was used to send mock
requests to test the REST APIs.  Travis-CI integration is also implemented, with tests running against Oracle JDK 7
and Open JDK 7.

## Getting Started ##

Clone the project, run <code>mvn compile test-compile</code> to download dependencies and compile the project.
Java 7 is required.  To run the tests, execute <code>mvn test</code>.

An Ant build file was created to simplify starting the server to run the application.  Run <code>ant tomcat</code>
to start the application using an embedded Tomcat 7 server (and embedded HSQL database).
