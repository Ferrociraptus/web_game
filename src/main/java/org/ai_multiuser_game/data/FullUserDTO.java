package org.ai_multiuser_game.data;

import org.ai_multiuser_game.entities.UserEntity;

public class FullUserDTO {
    public Long id;
    public String username;
    public String password;
    public String role;
    public String firstName;
    public String secondName;

    // json required
    public FullUserDTO() {}

    public FullUserDTO(Long id, String username, String password, String role){
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public FullUserDTO(String username, String password){
        this.username = username;
        this.password = password;
        this.role = "user";
    }

    public FullUserDTO(StartupUserDTO user, String password){
        this.username = user.username;
        this.password = password;
        this.role = user.role;
    }

    public FullUserDTO(UserEntity user){
        this.username = user.username;
        this.password = user.password;
        this.role = user.role;
    }
}
