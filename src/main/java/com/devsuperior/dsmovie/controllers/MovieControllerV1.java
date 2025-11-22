package com.devsuperior.dsmovie.controllers;

import com.devsuperior.dsmovie.dtos.MovieDTO;
import com.devsuperior.dsmovie.dtos.MovieGenreDTO;
import com.devsuperior.dsmovie.services.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/v1/movies")
public class MovieControllerV1 {

	@Autowired
	private MovieService service;

	@GetMapping(produces = "application/json")
    @Operation(
            description = "Get all movies",
            summary = "List all movies",
            responses = {
                    @ApiResponse(description = "Ok", responseCode = "200")
            }
    )
	public Page<MovieGenreDTO> findAll(
			@RequestParam(value="title", defaultValue = "") String title, 
			Pageable pageable) {
		return service.findAllMovieGenre(title, pageable);
	}

	@GetMapping(value = "/{id}",
                produces = "application/json")
    @Operation(
            description = "Get movie by id",
            summary = "List movie by id",
            responses = {
                    @ApiResponse(description = "Ok", responseCode = "200"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            }
    )
	public MovieGenreDTO findById(@PathVariable Long id) {
		return service.findByIdMovieGenre(id);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping(produces = "application/json")
    @Operation(
            description = "Create a new movie",
            summary = "Create a new movie",
            responses = {
                    @ApiResponse(description = "Created", responseCode = "201"),
                    @ApiResponse(description = "Bad Request", responseCode = "400"),
                    @ApiResponse(description = "Unauthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
                    @ApiResponse(description = "Unprocessable Entity", responseCode = "422")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<MovieDTO> insert(@Valid @RequestBody MovieDTO dto) {
		dto = service.insert(dto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
		return ResponseEntity.created(uri).body(dto);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping(value = "/{id}",
                produces = "application/json")
    @Operation(
            description = "Update a movie",
            summary = "Update a movie",
            responses = {
                    @ApiResponse(description = "Ok", responseCode = "200"),
                    @ApiResponse(description = "Bad Request", responseCode = "400"),
                    @ApiResponse(description = "Unauthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404"),
                    @ApiResponse(description = "Unprocessable Entity", responseCode = "422")

            }
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<MovieDTO> update(@PathVariable Long id, @Valid @RequestBody MovieDTO dto) {
		dto = service.update(id, dto);
		return ResponseEntity.ok().body(dto);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping(value = "/{id}")
    @Operation(
            description = "Delete a movie",
            summary = "Delete a movie",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "204"),
                    @ApiResponse(description = "Bad Request", responseCode = "400"),
                    @ApiResponse(description = "Unauthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404"),
                    @ApiResponse(description = "Unprocessable Entity", responseCode = "422")

            }
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<MovieDTO> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
