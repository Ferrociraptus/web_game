package org.ai_multyuser_game.data;

import org.ai_multyuser_game.entities.UserEntity;

public class FullUserDTO {
    public Long id;
    public String username;
    public String password;
    public String role;
    public String firstName;
    public String secondName;

    // json required
    public FullUserDTO() {}

    public FullUserDTO(Long id, String username, String password, String role, String firstName, String secondName){
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.firstName = firstName;
        this.secondName = secondName;
    }

    public FullUserDTO(String username, String password, String firstName, String secondName){
        this.username = username;
        this.password = password;
        this.role = "user";
        this.firstName = firstName;
        this.secondName = secondName;
    }

    public FullUserDTO(StartupUserDTO user, String password){
        this.username = user.username;
        this.password = password;
        this.role = user.role;
        this.firstName = user.firstName;
        this.secondName = user.secondName;
    }

    public FullUserDTO(UserEntity user){
        this.username = user.username;
        this.password = user.password;
        this.role = user.role;
        this.firstName = user.firstName;
        this.secondName = user.secondName;
    }
}
