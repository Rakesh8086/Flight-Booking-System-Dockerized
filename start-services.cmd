@echo off
echo Starting Service Registry...
start "" java -jar service-registry\target\service-registry-0.0.1-SNAPSHOT.jar
timeout /t 5
echo Starting API Gateway...
start "" java -jar api-gateway\target\api-gateway-0.0.1-SNAPSHOT.jar
timeout /t 2
echo Starting Authentication Service...
start "" java -jar authentication-service\target\spring-login-0.0.1-SNAPSHOT.jar
timeout /t 2
echo Starting Flight Service...
start "" java -jar flight-service\target\flight-service-0.0.1-SNAPSHOT.jar
timeout /t 2
echo Starting Booking Service...
start "" java -jar booking-service\target\booking-service-0.0.1-SNAPSHOT.jar
timeout /t 2
echo All services started!
pause
