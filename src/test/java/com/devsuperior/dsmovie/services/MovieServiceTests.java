package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.services.exceptions.DatabaseException;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class MovieServiceTests {
	
	@InjectMocks
    @Spy
	private MovieService movieService;

    @Mock
    private MovieRepository movieRepository;

    private MovieEntity movie;

    private MovieDTO movieDTO;

    private List<MovieEntity> movies;

    private String name;

    private Long existingMovieId,
                nonExistingMovieId,
                dependantMovieId;

    @BeforeEach
    void setUp() {
        name = "Test Movie";
        movie = MovieFactory.createMovieEntity();
        movieDTO = MovieFactory.createMovieDTO();
        movies = List.of(movie);
        existingMovieId = movie.getId();
        nonExistingMovieId = 2L;
        dependantMovieId = 3L;


        Page<MovieEntity> page = new PageImpl<>(movies);

        Mockito.when(movieRepository.searchByTitle(Mockito.eq(name), Mockito.any(Pageable.class))).thenReturn(page);

        Mockito.when(movieRepository.findById(existingMovieId)).thenReturn(Optional.of(movie));
        Mockito.when(movieRepository.findById(nonExistingMovieId)).thenReturn(Optional.empty());

        Mockito.when(movieRepository.save(Mockito.any())).thenReturn(movie);

        Mockito.when(movieRepository.getReferenceById(existingMovieId)).thenReturn(movie);
        Mockito.when(movieRepository.getReferenceById(nonExistingMovieId)).thenThrow(EntityNotFoundException.class);

        Mockito.when(movieRepository.existsById(existingMovieId)).thenReturn(true);
        Mockito.when(movieRepository.existsById(dependantMovieId)).thenReturn(true);
        Mockito.when(movieRepository.existsById(nonExistingMovieId)).thenThrow(ResourceNotFoundException.class);

        Mockito.doNothing().when(movieRepository).deleteById(existingMovieId);

        Mockito.doThrow(DatabaseException.class).when(movieRepository).deleteById(dependantMovieId);
        Mockito.doThrow(DatabaseException.class).when(movieRepository).deleteById(nonExistingMovieId);

    }
	
	@Test
	public void findAllShouldReturnPagedMovieDTO() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<MovieDTO> result = movieService.findAll(name, pageRequest);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals(result.iterator().next().getTitle(), name);
	}
	
	@Test
	public void findByIdShouldReturnMovieDTOWhenIdExists() {
	    MovieDTO result = movieService.findById(existingMovieId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(name, result.getTitle());

    }
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
       Throwable exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
           movieService.findById(nonExistingMovieId);
       });

       Assertions.assertEquals("Recurso não encontrado", exception.getMessage());
    }
	
	@Test
	public void insertShouldReturnMovieDTO() {
        MovieDTO result = movieService.insert(movieDTO);

        Assertions.assertNotNull(result);

        Assertions.assertEquals(result.getId(), movieDTO.getId());
        Assertions.assertEquals(result.getTitle(), movieDTO.getTitle());
        Assertions.assertEquals(result.getScore(), movieDTO.getScore());
        Assertions.assertEquals(result.getCount(), movieDTO.getCount());
        Assertions.assertEquals(result.getImage(), movieDTO.getImage());
    }
	
	@Test
	public void updateShouldReturnMovieDTOWhenIdExists() {
        MovieDTO result = movieService.update(existingMovieId, movieDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), movieDTO.getId());
        Assertions.assertEquals(result.getTitle(), movieDTO.getTitle());
        Assertions.assertEquals(result.getScore(), movieDTO.getScore());
        Assertions.assertEquals(result.getCount(), movieDTO.getCount());
        Assertions.assertEquals(result.getImage(), movieDTO.getImage());
    }
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Throwable exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            movieService.update(nonExistingMovieId, movieDTO);
        });

        Assertions.assertEquals("Recurso não encontrado", exception.getMessage());
    }
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
        Assertions.assertDoesNotThrow(() -> {
            movieService.delete(existingMovieId);
        });
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Throwable exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            movieService.delete(nonExistingMovieId);
        });
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
        Throwable exception = Assertions.assertThrows(DatabaseException.class, () -> {
            movieService.delete(dependantMovieId);
        });
	}
}
