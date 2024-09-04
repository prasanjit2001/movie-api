package com.movie.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movie.dto.MovieDto;
import com.movie.dto.MoviePageResponse;
import com.movie.exception.EmptyFileException;
import com.movie.service.MovieService;
import com.movie.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/movie")
public class MovieController {
    @Autowired
    private MovieService movieService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<MovieDto>addMovie(@RequestPart MultipartFile file,
                                            @RequestPart String movieDto
                                            ) throws IOException, EmptyFileException {

     if(file.isEmpty()){
         throw new EmptyFileException("File is Empty !Please Send Another file!");
     }
    MovieDto movieDto1= convertToMovieDto(movieDto);
    return new ResponseEntity<>(movieService.addMovie(movieDto1,file), HttpStatus.CREATED);
    }
    private MovieDto convertToMovieDto(String movieDtoObject) throws JsonProcessingException {
        ObjectMapper objectMapper=new ObjectMapper();
        return objectMapper.readValue(movieDtoObject,MovieDto.class);

    }

    @GetMapping("{movieId}")
    public ResponseEntity<MovieDto>getMovie(@PathVariable Integer movieId){
        return ResponseEntity.ok(movieService.getMovie(movieId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<MovieDto>>getAll(){
        return ResponseEntity.ok(movieService.getAllMovie());
    }

    @PutMapping("/update/{movieId}")
    public ResponseEntity<MovieDto>updateMovie(@PathVariable Integer movieId,
                                               @RequestPart MultipartFile file,
                                               @RequestPart String movieDtoObj
    ) throws IOException {
        if(file.isEmpty())file=null;
        MovieDto movieDto=convertToMovieDto(movieDtoObj);
        return ResponseEntity.ok(movieService.updateMovie(movieId,movieDto,file));
    }

    @DeleteMapping("/delete/{movieId}")
    public ResponseEntity<String>deleteMovie(@PathVariable Integer movieId) throws IOException {
        return  ResponseEntity.ok(movieService.deleteMovie(movieId));
    }

    @GetMapping("/allMoviesPage")
    public ResponseEntity<MoviePageResponse>getMoviesWithPagination(
        @RequestParam (defaultValue = AppConstants.PAGE_NUMBER,required = false)    Integer pageNumber,
        @RequestParam (defaultValue = AppConstants.PAGE_SIZE,required = false)  Integer pageSize

    ){
        return ResponseEntity.ok(movieService.getAllMoviesWithPagination(pageNumber,pageSize));
    }

    @GetMapping("/allMoviesPageSort")
    public ResponseEntity<MoviePageResponse>getMoviesWithPaginationSorting(
            @RequestParam (defaultValue = AppConstants.PAGE_NUMBER,required = false)    Integer pageNumber,
            @RequestParam (defaultValue = AppConstants.PAGE_SIZE,required = false)  Integer pageSize,
              @RequestParam (defaultValue = AppConstants.SORT_BY,required = false)  String sortBy,
            @RequestParam (defaultValue = AppConstants.SORT_DIR,required = false)  String dir

    ){
        return ResponseEntity.ok(movieService.getAllMoviesWithPaginationAndSorting(pageNumber,pageSize,sortBy,dir));
    }

}
