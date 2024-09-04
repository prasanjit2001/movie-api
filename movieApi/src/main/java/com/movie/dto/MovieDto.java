package com.movie.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieDto {
    private Integer movieId;
    @NotBlank(message = "Please provide movies title")
    private String title;
    @NotBlank(message = "Please provide movies director")
    private String director ;
    @NotBlank(message = "Please provide movies studio")
    private String studio ;
    private Set<String> movieCast;
    private Integer releaseYear;
    @NotBlank(message = "Please provide movies poster")
    private String poster;
    private String posterUrl;
}
