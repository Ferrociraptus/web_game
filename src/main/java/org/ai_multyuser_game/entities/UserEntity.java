package org.ai_multyuser_game.entities;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;

import javax.persistence.*;

import org.ai_multyuser_game.data.*;

@Entity
//@Table(name = "user")
@PersistenceUnit
@UserDefinition
public class UserEntity extends PanacheEntity {

    @Username
    public String username;
    @Password
    public String password;
    @Roles
    public String role = "user";

    public String firstName;
    public String secondName;

    public UserEntity() {}

    public UserEntity(String username, String password){
        this.username = username;
        this.password = BcryptUtil.bcryptHash(password);
        this.role = "user";
    }

    public UserEntity(String username, String password, String role){
        this.username = username;
        this.password = BcryptUtil.bcryptHash(password);
        this.role = role;
    }


    public UserEntity(String username, String password, String role, String firstName, String secondName){
        this.username = username;
        this.password = BcryptUtil.bcryptHash(password);
        this.role = role;
        this.firstName = firstName;
        this.secondName = secondName;
    }

    public UserEntity(String username, String password, String firstName, String secondName){
        this.username = username;
        this.password = BcryptUtil.bcryptHash(password);
        this.role = "user";
        this.firstName = firstName;
        this.secondName = secondName;
    }

    public UserEntity(StartupUserDTO user){
        this.username = user.username;
        this.password = user.password;
        this.role = user.role;
        this.firstName = user.firstName;
        this.secondName = user.secondName;
    }

    /**
     * Adds a new user to the database
     * @param username the username
     * @param password the unencrypted password (it will be encrypted with bcrypt)
     * @param role the comma-separated roles
     */
    public static void add(String username, String password, String role, String firstName, String secondName) {
        UserEntity user = new UserEntity();
        user.username = username;
        user.password = BcryptUtil.bcryptHash(password);
        user.role = role;
        user.firstName = firstName;
        user.secondName = secondName;
        user.persist();
    }

    public FullUserDTO toFullDTO(){
        return new FullUserDTO(id, username, password, role, firstName, secondName);
    }
}