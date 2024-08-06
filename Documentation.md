   **Table of Contents**
    1. Introduction
    2. Architecture Overview
    3. Controller Layer
    4. Service Layer
    5. Exception Handling
    6. Utilities
    7. Data Management
    8. API Documentation
    9. Dockerization
    10. Kubernetes Deployment

2. **Introduction** 
    The Crypto Recommendation Service is a Spring Boot application designed 
    to help XM developers make informed investments in cryptocurrencies. 
    It reads cryptocurrency price data from CSV files, calculates statistics, 
    and exposes RESTful endpoints for querying this data.


2. **Architecture Overview**
    The application is structured into several layers:
    Controller Layer: Handles HTTP requests and responses.
    Service Layer: Contains the business logic.
    Exception Handling: Manages application-specific and general exceptions.
    Utilities: Provides auxiliary functionality such as reading CSV files, calculating statistics, and rate limiting.
    Swagger Documentation: Provides API documentation using SpringDoc OpenAPI.

3. **Controller Layer** 

    _CryptoController_ 
    The CryptoController handles requests related to cryptocurrency statistics. It includes endpoints for:
    Retrieving crypto statistics within a specified date range.
    Getting statistics for a specific cryptocurrency.
    Finding the cryptocurrency with the highest normalized range for a specific day.
    Listing all supported cryptocurrencies.

    _DataController_
    The DataController handles requests for uploading cryptocurrency data. It includes an endpoint for uploading CSV files containing crypto price data.


4. **Service Layer**
    CryptoService
    The CryptoService (impl: CryptoServiceImpl) class implements the business logic for handling cryptocurrency data and statistics. 
    
    It provides methods for:
    Retrieving statistics for a specific cryptocurrency.    
    Calculating and returning crypto statistics within a specified date range.
    Finding the cryptocurrency with the highest normalized range for a specific day.
    Listing all supported cryptocurrencies.

5. **Exception Handling**
    NotFoundException
    A custom exception thrown when a requested resource is not found, such as when querying for a cryptocurrency that is not supported.

    GlobalExceptionHandler
    A centralized exception handler to manage exceptions and provide meaningful error responses. It handles:

    NotFoundException: Returns a 404 Not Found response.
    IllegalArgumentException: Returns a 400 Bad Request response.

6. **Utilities**
    _CryptoCsvReader_
    The CryptoCsvReader component is responsible for reading and processing cryptocurrency price data from CSV files. It includes methods for:

    Loading data from CSV files in a specified directory.
    Processing uploaded CSV files via API.
    CryptoStatsCollector
    A custom collector used to calculate statistics (min, max, newest, oldest) for cryptocurrency data.

    _DateUtils_
    A utility class for parsing dates and converting them to timestamps representing the start and end of a day.

    _RateLimitingFilter_
    A filter that applies rate limiting to incoming requests based on IP address to prevent abuse and ensure fair usage of the service.

7. **Data Management**
    Loading Data from Folder
    The service automatically loads cryptocurrency price data from CSV files located in a specified directory at startup. The CryptoCsvReader reads these files and processes the data to be used by the service.
    
    Uploading Data via API
    The DataController provides an endpoint to upload cryptocurrency price data via CSV files. The uploaded files are processed and the data is stored for querying through the service's endpoints.
    It can be improved that to check if the file is already uploaded and if so, update the data instead of creating a new entry. This logic is not here bcs database interaction just has been mocked. 

8. **API Documentation**
    The application provides API documentation using SpringDoc OpenAPI. The Swagger UI can be accessed at http://localhost:8080/swagger-ui.html. It lists all available endpoints, request/response formats, and example responses.

9. **Dockerization**
    The application can be containerized using Docker. A Dockerfile is provided to build an image and run the application in a container. The application can be accessed at http://localhost:8080 when running in a container.
    To build the image: docker run -p 8080:8080 bago1/crypto:latest
    To start the project the script named build_and_run.sh also can be used via docker-launch. 

10. **Kubernetes Deployment**
    The application can be deployed to a Kubernetes cluster using a deployment and service configuration. The deployment ensures that the application is running and available, while the service provides a stable endpoint for accessing the application.
    To deploy the application: kubectl apply -f k8s/deployment.yaml
    To expose the application: kubectl apply -f k8s/service.yaml

11. **Unit Testing**
    The application includes unit tests for the service layer using JUnit and Mockito. The tests cover various scenarios to ensure the correctness of the business logic and exception handling.

12. 