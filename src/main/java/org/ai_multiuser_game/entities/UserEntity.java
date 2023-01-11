package org.ai_multiuser_game.entities;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;

import javax.persistence.*;
import javax.transaction.Transactional;

import org.ai_multiuser_game.data.*;
import org.hibernate.validator.constraints.UniqueElements;

@Entity
@UserDefinition
public class UserEntity extends PanacheEntity {

    @Username
    @Column(unique = true)

    public String username;
    @Password
    public String password;
    @Roles
    public String role = "user";

    public UserEntity() {super();}

    public UserEntity(String username, String password){
        super();
        this.username = username;
        this.password = BcryptUtil.bcryptHash(password);
        this.role = "user";
    }

    public UserEntity(String username, String password, String role){
        super();
        this.username = username;
        this.password = BcryptUtil.bcryptHash(password);
        this.role = role;
    }

    public UserEntity(StartupUserDTO newUser) {
        super();
        this.username = newUser.username;
        this.password = BcryptUtil.bcryptHash(newUser.password);
    }

    @Transactional
    public static UserEntity createNewUser(StartupUserDTO newUser){
        var user = new UserEntity(newUser);
        user.persistAndFlush();
        return user;
    }
    @GeneratedValue
    public Long getId(){
        return id;
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
        user.persistAndFlush();
    }

    public static UserEntity getByUsername(String username){
        return find("username", username).firstResult();
    }

    public static UserEntity getFromUserDTO(UserDTO user){
        return getByUsername(user.username);
    }
    public FullUserDTO toFullDTO(){
        return new FullUserDTO(id, username, password, role);
    }
}