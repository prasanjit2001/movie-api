package com.movie.dto;

import java.util.List;

public record MoviePageResponse (List<MovieDto> movieDtoList,
                                 Integer pageNumber,
                                 Integer pageSize,
                                 long totalElements,
                                 int totalPages,
                                 boolean isLastPage

                                 ){

}
