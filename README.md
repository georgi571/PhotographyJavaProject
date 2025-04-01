# PhotographyJavaProject

# Angular 19 + Spring Boot (Java 21) + Kafka (3.9.0)

This project is a full-stack application using Angular (Frontend), Spring Boot (Backend), and Kafka (Messaging System). 

# Prerequisites

Ensure you have the following installed before proceeding:

Docker Desktop - https://www.docker.com/get-started/

Node.js (for Angular) - v20.14.0 or the latest stable version.

Java 21+ (for Spring Boot)

Kafka - Docker image: apache/kafka:3.9.0 or https://kafka.apache.org/downloads

Maven (for dependency management)

Angular CLI (if not installed, install it globally using npm install -g @angular/cli)

Cloudinary Account for image storage

Gmail Account for sending emails

# Installation & Setup

1. Clone the Repository:  https://github.com/georgi571/PhotographyJavaProject.git

2. Start Docker Desktop and then start Kafka

Running the Frontend (Angular)

1. Navigate to the Frontend Directory - cd .\PhotographyAngular\

1. Install Dependencies - npm install

2. Start the Angular - npm serve or from package.json file press start button on "start: ng serve" on row 6

Running the Backend (Spring)

1. Start all 5 microservices
   Each microservice manages a specific function:
   - PhotographyJava (User Management)
   - Challenges (Challenge Management)
   - Contacts (Contact Us Management)
   - Leaderboards (Leaderboards Management)
   - Reports (Reports Management)

# License

This project is licensed under MIT License.
