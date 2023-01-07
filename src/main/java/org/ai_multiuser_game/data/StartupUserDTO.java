package org.ai_multiuser_game.data;

public class StartupUserDTO {
    public String username;

    public String password;
    public String role;

    public StartupUserDTO() {}

    public StartupUserDTO(String username, String password, String role, String firstName, String secondName){
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public StartupUserDTO(FullUserDTO user){
        this.username = user.username;
        this.role = user.role;
    }
}
