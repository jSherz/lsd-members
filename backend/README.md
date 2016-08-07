# LUU Skydivers Management System

## Prerequisites

* PostgreSQL database server (v9.5)

* Role and database setup for the website

* Environment variables that configure the above:

    ```bash
    export LSD_DB_URL=jdbc:postgresql://localhost/luskydive
    export LSD_DB_USERNAME=luskydive
    export LSD_DB_PASSWORD=luskydive
    ```

## Backend

1. Launch SBT:

        $ sbt

2. Compile everything and run all tests:

        > test

3. Start the application:

        > run

4. Browse to [http://localhost:8080](http://localhost:8080/)
