The project has been written to learn Spring Boot tools and technologies using API Gateway, Zipkin, Lombok, Spring Cloud Eureka, JPA, Hibernate and RabbitMQ. After that, the project has been containerized using Docker. The circuit breaker mechanism has implemented using Resilience4J. The project uses PostgreSQL as the database. If you want, you may use H2Database which is an embedded databse for the small applications. The configurations are included in the application.properties file.  
The project consists of three microservices named jobms, companyms and reviewms and one api gateway microservice.
Source:
- Part 1: https://www.youtube.com/watch?v=BLlEgtp2_i8
- Part 2: https://www.youtube.com/watch?v=EeQRAxXWDF4  
I would like to thank Faisal Memon for the awesome content!

Installation:
- Dockerize every microservice using the maven wrapper "./mvnw spring-boot:build-image "-Dspring-boot.build-image.imageName=docker-username/image-name". 
- After that push the docker images to Docker Hub. "docker push docker-username/image-name"
- Update image names in docker-compose file as image-name for every microservice.
- Run in the detach mode. docker-compose up -d
- Set up company, job and review databases using pgAdmin.
- Run docker-compose up -d again.
- Check localhost:8761 to see all four microservices are up and running.

