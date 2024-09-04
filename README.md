# Movie API

The **Movie API** is a RESTful service that allows users to view movies and their associated posters. Admin users have the ability to post new movies along with their posters, while regular users can view the movies. Additionally, the API provides a "Forgot Password" feature, which allows users to reset their password via email.

## Features

- **Movie Management**:
  - Admin users can add new movies with details such as title, director, studio, cast, release year, and poster.
  - Regular users can view the list of movies but cannot modify or add new entries.

- **Poster Upload**:
  - Admins can upload a movie's poster when adding a movie.

- **User Authentication**:
  - Only admins have the privilege to add or modify movies.
  - Regular users have view-only access to the movie listings.

- **Forgot Password**:
  - Users can request a password reset by providing their email.
  - A reset link or OTP will be sent to the user's registered email address for password recovery.

## API Endpoints

### Authentication Endpoints

- **POST /api/v1/auth/register**: Registers a new user.
- **POST /api/v1/auth/login**: Logs in a user and returns a JWT token.
- **POST /api/v1/auth/refresh**: Refreshes the JWT token using a refresh token.

### Movie Endpoints

- **POST /api/v1/movie/add**: Adds a new movie (Admin only). Requires a movie DTO and a poster file.
- **GET /api/v1/movie/{movieId}**: Fetches details of a specific movie by ID.
- **GET /api/v1/movie/all**: Fetches a list of all movies.
- **PUT /api/v1/movie/update/{movieId}**: Updates an existing movie (Admin only). Requires a movie DTO and a poster file.
- **DELETE /api/v1/movie/delete/{movieId}**: Deletes a specific movie by ID (Admin only).
- **GET /api/v1/movie/allMoviesPage**: Fetches a paginated list of movies.
- **GET /api/v1/movie/allMoviesPageSort**: Fetches a paginated and sorted list of movies.

### Forgot Password Endpoints

- **POST /forgotPassword/verifyMail/{email}**: Sends an OTP to the provided email for password reset.
- **POST /forgotPassword/verifyOtp/{otp}/{email}**: Verifies the provided OTP for the given email.
- **POST /forgotPassword/changePassword/{email}**: Changes the password for the given email after OTP verification.

## Technologies Used

- **Spring Boot**: Backend framework to create RESTful APIs.
- **Spring Security**: For securing the API and handling user roles and permissions.
- **JavaMailSender**: To handle email sending for the "Forgot Password" feature.
- **JPA/Hibernate**: To interact with the MySQL database.
- **MySQL**: Database to store movie and user data.
- **Lombok**: For reducing boilerplate code (e.g., getters, setters, constructors).

## How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/movie-api.git
