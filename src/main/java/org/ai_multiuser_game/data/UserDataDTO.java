package org.ai_multiuser_game.data;

import org.ai_multiuser_game.entities.UserEntity;

public class UserDataDTO {
    public String username;

    public UserDataDTO() {}

    public UserDataDTO(String username){
        this.username = username;
    }

    public UserDataDTO(FullUserDTO user){
        this.username = user.username;
    }

    public UserDataDTO(StartupUserDTO user){
        this.username = user.username;
    }

    public UserDataDTO(UserEntity user){
        this.username = user.username;
    }
}
