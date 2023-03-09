package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;

import java.time.LocalDate;

public class UserPostDTO {

    private String password;

    private String username;

    private Long id;

//    private UserStatus status;
//    private LocalDate creation_date;
//    private LocalDate birthday;
//    private String token;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}
}
