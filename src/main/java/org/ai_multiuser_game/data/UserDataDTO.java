package org.ai_multiuser_game.data;

import org.ai_multiuser_game.entities.UserEntity;

public class UserDataDTO {
    public String username;
    public String firstName;
    public String secondName;

    public UserDataDTO() {}

    public UserDataDTO(String username, String firstName, String secondName){
        this.username = username;
        this.firstName = firstName;
        this.secondName = secondName;
    }

    public UserDataDTO(FullUserDTO user){
        this.username = user.username;
        this.firstName = user.firstName;
        this.secondName = user.secondName;
    }

    public UserDataDTO(StartupUserDTO user){
        this.username = user.username;
        this.firstName = user.firstName;
        this.secondName = user.secondName;
    }

    public UserDataDTO(UserEntity user){
        this.username = user.username;
        this.firstName = user.firstName;
        this.secondName = user.secondName;
    }
}
