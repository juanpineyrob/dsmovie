package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.projections.UserDetailsProjection;
import com.devsuperior.dsmovie.repositories.UserRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.UserDetailsFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import com.devsuperior.dsmovie.utils.CustomUserUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class UserServiceTests {

	@InjectMocks
	private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomUserUtil customUserUtil;

    private UserEntity userRoleClient,
                    userRoleAdmin;

    private List<UserDetailsProjection> roleClientUserDetails,
                                        roleAdminUserDetails;

    private String existingEmail,
            nonExistingEmail;

    @BeforeEach
    void setup() {
        userRoleClient = UserFactory.createUserEntity();

        existingEmail = userRoleClient.getUsername();
        nonExistingEmail = "non-ex@ds.com";


        roleClientUserDetails = UserDetailsFactory.createCustomClientUser(existingEmail);

        Mockito.when(userRepository.findByUsername(existingEmail)).thenReturn(Optional.of(userRoleClient));
        Mockito.when(userRepository.findByUsername(nonExistingEmail)).thenReturn(Optional.empty());

        Mockito.when(userRepository.searchUserAndRolesByUsername(existingEmail)).thenReturn(roleClientUserDetails);
        Mockito.when(userRepository.searchUserAndRolesByUsername(nonExistingEmail)).thenReturn(List.of());

    }

	@Test
	public void authenticatedShouldReturnUserEntityWhenUserExists() {
        Mockito.when(customUserUtil.getLoggedUsername()).thenReturn(existingEmail);

        UserEntity result = userService.authenticated();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingEmail, result.getUsername());
	}

	@Test
	public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
        Mockito.when(customUserUtil.getLoggedUsername()).thenReturn(nonExistingEmail);

        Throwable exception = Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            UserEntity result = userService.authenticated();
        });

        Assertions.assertEquals("Invalid user", exception.getMessage());
	}

	@Test
	public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {
        UserDetails result = userService.loadUserByUsername(existingEmail);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getUsername(), existingEmail);
	}

	@Test
	public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
        Throwable exception = Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            UserDetails result = userService.loadUserByUsername(nonExistingEmail);
        });

        Assertions.assertEquals("Email not found", exception.getMessage());
	}
}
