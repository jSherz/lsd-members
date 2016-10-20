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

* Twilio account with configuration:

    ```bash
    export LSD_TWILIO_ACCOUNT_SID=123
    export LSD_TWILIO_AUTH_TOKEN=123
    export LSD_TWILIO_MESSAGING_SERVICE_SID=123
    ```

* Random key used to authenticate Twilio for received messages:

    ```bash
    export LSD_TEXT_MESSAGE_API_KEY=123
    ```

## Backend

1. Launch SBT:

        $ sbt

2. Compile everything and run all unit tests:

        > test

3. Switch environment variables to a test database and run integration tests:

        $ export ...
        $ sbt
        > it:test

4. Start the application:

        > run

5. Browse to [http://localhost:8080](http://localhost:8080/)
