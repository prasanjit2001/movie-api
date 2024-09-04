package com.movie.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer movieId;
    @Column(nullable = false,length = 200)
    @NotBlank(message = "Please provide movies title")
    private String title;
    @Column(nullable = false)
    @NotBlank(message = "Please provide movies director")
    private String director ;
    @Column(nullable = false)
    @NotBlank(message = "Please provide movies studio")
    private String studio ;
    @ElementCollection
    @CollectionTable(name = "movie_cast")
    private Set<String> movieCast;
    @Column(nullable = false)
    private Integer releaseYear;
    @Column(nullable = false)
    @NotBlank(message = "Please provide movies poster")
    private String poster;
}
