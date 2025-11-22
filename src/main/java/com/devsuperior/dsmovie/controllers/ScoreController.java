package com.devsuperior.dsmovie.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devsuperior.dsmovie.dtos.MovieDTO;
import com.devsuperior.dsmovie.dtos.ScoreDTO;
import com.devsuperior.dsmovie.services.ScoreService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/scores")
@Tag(name = "Movies", description = "Controller for Movie")
public class ScoreController {
	
	@Autowired
	private ScoreService service;
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT')")
	@PutMapping(produces = "application/json")
    @Operation(
            description = "Save a score",
            summary = "Save a score",
            responses = {
                    @ApiResponse(description = "Created", responseCode = "201"),
                    @ApiResponse(description = "Bad Request", responseCode = "400"),
                    @ApiResponse(description = "Unauthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
                    @ApiResponse(description = "Unprocessable Entity", responseCode = "422")
            }
    )
	public ResponseEntity<MovieDTO> saveScore(@Valid @RequestBody ScoreDTO dto) {
		MovieDTO movieDTO = service.saveScore(dto);
		return ResponseEntity.ok().body(movieDTO);
	}
}
