package org.ai_multiuser_game.data;

import org.ai_multiuser_game.entities.UserEntity;

public class UserDTO {
    public String username;

    public Long id;
    public String firstName;
    public String secondName;

    public UserDTO() {}

    public UserDTO(Long id, String username, String password, String firstName, String secondName){
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.secondName = secondName;
    }

    public UserDTO(FullUserDTO user){
        this.id = user.id;
        this.username = user.username;
        this.firstName = user.firstName;
        this.secondName = user.secondName;
    }

    public UserDTO(UserEntity user){
        this.id = user.id;
        this.username = user.username;
        this.firstName = user.firstName;
        this.secondName = user.secondName;
    }
}
