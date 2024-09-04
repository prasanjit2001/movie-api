package com.movie.service;


import com.movie.dto.MovieDto;
import com.movie.dto.MoviePageResponse;
import com.movie.entities.Movie;
import com.movie.exception.EmptyFileException;
import com.movie.exception.FileExistsException;
import com.movie.exception.MovieNotFoundException;
import com.movie.repositories.MovieRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Pageable;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService {

    @Value("${projects.poster}")
    private String path;
    @Value("${base.url}")
    String baseUrl;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private FileService fileService;
    @Autowired
   private ModelMapper modelMapper;

    public MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException, EmptyFileException {
        // Get the original filename of the uploaded file
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new EmptyFileException("Filename cannot be null");
        }

        // Construct the file path
        Path filePath = Paths.get(path, originalFilename);

        // Check if the file already exists
        if (Files.exists(filePath)) {
            throw new FileExistsException("File already exists. Please enter another file name.");
        }


        // Upload the file
        String uploadedFileName = fileService.uploadFile(path, file);
        System.out.println("Uploaded file name: " + uploadedFileName);

        // Set the value of field 'poster' as filename
        movieDto.setPoster(uploadedFileName);

        // Save the DTO into the database; map DTO to entity
        Movie movie = modelMapper.map(movieDto, Movie.class);
        Movie savedMovie = movieRepository.save(movie);

        // Generate the poster URL
        String posterUrl = baseUrl + "/file/" + uploadedFileName;
        System.out.println("Generated poster URL: " + posterUrl);

        // Set the poster URL in the DTO
        movieDto.setPosterUrl(posterUrl);

        // Map the saved entity back to DTO
        MovieDto addedMovieDto = modelMapper.map(savedMovie, MovieDto.class);
        addedMovieDto.setPosterUrl(posterUrl);  // Ensure posterUrl is set

        System.out.println("Added Movie DTO: " + addedMovieDto);

        return addedMovieDto;
    }




    public MovieDto getMovie(Integer movieId){
       Movie movie = movieRepository.findById(movieId)
               .orElseThrow(() -> new MovieNotFoundException("Movie not found with id: " + movieId));
        String posterUrl = baseUrl + "/file/" + movie.getPoster();
        MovieDto movieDto = modelMapper.map(movie, MovieDto.class);
        movieDto.setPosterUrl(posterUrl);
        return movieDto;
    }

    public List<MovieDto> getAllMovie() {
        List<Movie> movies = movieRepository.findAll();
        // Map each movie entity to MovieDto and set the poster URL
        return movies.stream()
                .map(movie -> {
                    // Map the Movie entity to MovieDto
                    MovieDto movieDto = modelMapper.map(movie, MovieDto.class);
                    String posterUrl = baseUrl + "/file/" + movie.getPoster();
                    movieDto.setPosterUrl(posterUrl);
                    return movieDto;
                })
                .collect(Collectors.toList());
    }

    public MovieDto updateMovie(Integer movieId, MovieDto movieDto, MultipartFile file) throws IOException {
        // Retrieve the existing movie from the database
        Movie existingMovie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with id: " + movieId));

        // Handle file update
        if (file != null && !file.isEmpty()) {
            // Delete the old poster file if it exists
            String oldPosterFileName = existingMovie.getPoster();
            if (oldPosterFileName != null && !oldPosterFileName.equals("default")) {
                Files.deleteIfExists(Paths.get(path, oldPosterFileName));
            }

            // Upload the new file and update the poster field
            String newPosterFileName = fileService.uploadFile(path, file);
            existingMovie.setPoster(newPosterFileName);
        } else if (movieDto.getPoster() != null && !movieDto.getPoster().equals("default")) {
            // Handle poster update from DTO if no file is provided
            existingMovie.setPoster(movieDto.getPoster());
        }

        // Update other fields from the DTO
        if (movieDto.getTitle() != null) {
            existingMovie.setTitle(movieDto.getTitle());
        }
        if (movieDto.getDirector() != null) {
            existingMovie.setDirector(movieDto.getDirector());
        }
        if (movieDto.getStudio() != null) {
            existingMovie.setStudio(movieDto.getStudio());
        }
        if (movieDto.getMovieCast() != null) {
            existingMovie.setMovieCast(movieDto.getMovieCast());
        }
        if (movieDto.getReleaseYear() != null) {
            existingMovie.setReleaseYear(movieDto.getReleaseYear());
        }

        // Save the updated movie
        Movie updatedMovie = movieRepository.save(existingMovie);

        // Generate the poster URL and map the entity to DTO
        String posterUrl = baseUrl + "/file/" + updatedMovie.getPoster();
        MovieDto updatedMovieDto = modelMapper.map(updatedMovie, MovieDto.class);
        updatedMovieDto.setPosterUrl(posterUrl);

        return updatedMovieDto;
    }



    public String deleteMovie(Integer movieId) throws IOException {

      Movie movie=  movieRepository.findById(movieId).orElseThrow(()->new MovieNotFoundException("Movie not found"));
      Integer id=movie.getMovieId();
      Files.deleteIfExists(Paths.get(path, movie.getPoster()));
      movieRepository.delete(movie);
      return "Movie Deleted With ID="+ id;

  }

    public MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Movie> moviePage = movieRepository.findAll(pageable);

        // Map each Movie entity to MovieDto and set the poster URL
        List<MovieDto> movieDtos = moviePage.getContent().stream()
                .map(movie -> {
                    MovieDto movieDto = modelMapper.map(movie, MovieDto.class);
                    String posterUrl = baseUrl + "/file/" + movie.getPoster();
                    movieDto.setPosterUrl(posterUrl);
                    return movieDto;
                })
                .collect(Collectors.toList());

        // Create and return the MoviePageResponse record
        return new MoviePageResponse(
                movieDtos,
                moviePage.getNumber(),
                moviePage.getSize(),
                moviePage.getTotalElements(),
                moviePage.getTotalPages(),
                moviePage.isLast()
        );
    }




    public MoviePageResponse getAllMoviesWithPaginationAndSorting(Integer pageNumber,Integer pageSize,String sortBy,String dir){
  Sort sort=dir.equalsIgnoreCase("asc")?Sort.by(sortBy).ascending()
                                                      :Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize,sort);
        Page<Movie> moviePage = movieRepository.findAll(pageable);

        // Map each Movie entity to MovieDto and set the poster URL
        List<MovieDto> movieDtos = moviePage.getContent().stream()
                .map(movie -> {
                    MovieDto movieDto = modelMapper.map(movie, MovieDto.class);
                    String posterUrl = baseUrl + "/file/" + movie.getPoster();
                    movieDto.setPosterUrl(posterUrl);
                    return movieDto;
                })
                .collect(Collectors.toList());

        // Create and return the MoviePageResponse record
        return new MoviePageResponse(
                movieDtos,
                moviePage.getNumber(),
                moviePage.getSize(),
                moviePage.getTotalElements(),
                moviePage.getTotalPages(),
                moviePage.isLast()
        );

    }


}
