package org.ai_multiuser_game.data;

import org.ai_multiuser_game.entities.UserEntity;

public class UserDTO {
    public String username;

    public Long id;

    public UserDTO() {}

    public UserDTO(Long id, String username, String password, String firstName, String secondName){
        this.id = id;
        this.username = username;
    }

    public UserDTO(FullUserDTO user){
        this.id = user.id;
        this.username = user.username;
    }

    public UserDTO(UserEntity user){
        this.id = user.id;
        this.username = user.username;
    }
}
