package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dtos.MovieDTO;
import com.devsuperior.dsmovie.dtos.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {
	
	@InjectMocks
	private ScoreService scoreService;

    @Mock
    private ScoreRepository scoreRepository;

    @Mock
    private UserService userService;

    @Mock
    private MovieRepository movieRepository;

    private ScoreEntity score;

    private ScoreDTO scoreDTO;

    private UserEntity user;

    private MovieEntity movie;

    @BeforeEach
    void setUp() {
        ScoreEntity score = ScoreFactory.createScoreEntity();
        user = UserFactory.createUserEntity();
        movie = MovieFactory.createMovieEntity();
        scoreDTO = ScoreFactory.createScoreDTO();

        Mockito.when(scoreRepository.saveAndFlush(Mockito.any())).thenReturn(score);
    }

	
	@Test
	public void saveScoreShouldReturnMovieDTO() {
        Mockito.when(userService.authenticated()).thenReturn(user);
        Mockito.when(movieRepository.findById(Mockito.any())).thenReturn(Optional.of(movie));
        Mockito.when(movieRepository.save(Mockito.any())).thenReturn(movie);

        MovieDTO movieDTO = scoreService.saveScore(scoreDTO);

        Assertions.assertNotNull(movieDTO);

	}
	
	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
        Mockito.when(userService.authenticated()).thenReturn(user);
        Mockito.when(movieRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Throwable exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            scoreService.saveScore(scoreDTO);
        });

        Assertions.assertEquals("Recurso não encontrado", exception.getMessage());

    }
}
