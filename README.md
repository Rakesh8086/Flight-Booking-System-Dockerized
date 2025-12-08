# Dockerized Flight Booking System using Microservices
1) Built using Java, MYSQL, Docker
2) Microservices style of implementation is followed
3) RabbitMQ is used for Notification service's message queue
4) Flight service and Booking Service are connected through Service Registry
5) API gateway is added to enable sending HTTP request using a common port
6) Used Eureka for service registry
7) Used Open Feign for inter service communication
8) Spring security is implemented using JWT authentication
9) The app contains 5 Post, 2 Get and 1 Delete endpoint
10) POST - Add flight, Search flights, Book Ticket, Signup, Signin
11) GET - View Ticket, View history of bookings
12) DELETE - Cancel Ticket
13) Used Sonar Cloud Quality analysis and implemented its suggestions
14) Dockerized all the microservices
