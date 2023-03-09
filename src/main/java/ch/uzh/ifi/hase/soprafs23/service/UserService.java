package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    public User createUser(User newUser) {
        checkIfUserExists(newUser);

        newUser.setToken(UUID.randomUUID().toString());
        newUser.setStatus(UserStatus.ONLINE);
        newUser.setCreation_date(LocalDate.now());

        // saves the given entity but data is only persisted in the database once
        // flush() is called
        newUser = userRepository.save(newUser);
        userRepository.flush();

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    /**
     * This is a helper method that will check the uniqueness criteria of the
     * username and the password
     * defined in the User entity. The method will do nothing if the input is unique
     * and throw an error otherwise.
     *
     * @param userToBeCreated
     * @throws org.springframework.web.server.ResponseStatusException
     * @see User
     */
    private void checkIfUserExists(User userToBeCreated) {
        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());
        User userByPassword = userRepository.findByPassword(userToBeCreated.getPassword());

        String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
        if (userByUsername != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "username", "is"));
        }
    }
    public User logIn(User userCredentials) {
        User userLogIn = userRepository.findByUsername(userCredentials.getUsername());

        if(userLogIn == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The username provided does not exist.");
        } else if(!Objects.equals(userLogIn.getPassword(), userCredentials.getPassword())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username and password provided do not match.");
        } else {
            userLogIn.setToken(UUID.randomUUID().toString());
            userLogIn.setStatus(UserStatus.ONLINE);
            return userLogIn;
        }
    }

    public Optional<User> findUser(Long id) {
        boolean result = userRepository.existsById(id);
        if(!result) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The user to be inspected does not exist.");
        }
        return userRepository.findById(id);
    }

    @Transactional
    public void updateUser(User userProfile) {
        String updatedUsername = userProfile.getUsername();
        LocalDate updatedBirthday = userProfile.getBirthday();
        UserStatus updatedStatus = userProfile.getStatus();

        User user = findUser(userProfile.getId()).get();

        if (updatedUsername != null && updatedUsername.length() >0 && !Objects.equals(updatedUsername, user.getUsername())) {
            user.setUsername(updatedUsername);
        }
        if (updatedBirthday != null && !Objects.equals(updatedBirthday, user.getBirthday())) {
            user.setBirthday(updatedBirthday);
        }
        if (updatedStatus != null && !Objects.equals(updatedStatus, user.getStatus())) {
            user.setStatus(updatedStatus);
        }
    }
}
