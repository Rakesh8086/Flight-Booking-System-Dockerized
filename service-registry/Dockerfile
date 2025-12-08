FROM maven:3.8.5-openjdk-17 
WORKDIR /service-registry 
COPY . . 
RUN mvn clean install -DskipTests
CMD ["mvn", "spring-boot:run", "-Dmaven.test.skip=true"]