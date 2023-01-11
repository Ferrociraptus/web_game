package org.ai_multiuser_game.services;

import io.quarkus.elytron.security.common.BcryptUtil;
import org.ai_multiuser_game.data.FullUserDTO;
import org.ai_multiuser_game.data.StartupUserDTO;
import org.ai_multiuser_game.data.UserDTO;
import org.ai_multiuser_game.data.UserDataDTO;
import org.ai_multiuser_game.entities.UserEntity;

import javax.inject.Singleton;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

@Singleton
public class UsersService {

    public List<FullUserDTO> getUsers(){
        return UserEntity.listAll().stream().map(e -> new FullUserDTO((UserEntity) e)).toList();
    }

    @Transactional
    public Long createUser(StartupUserDTO newUser){
        if (UserEntity.getByUsername(newUser.username) != null)
            throw new WebApplicationException("User with \"" + newUser.username + "\" username exist",
                    Response.status(409, "User with \"" + newUser.username + "\" username exist").build());

        var user = UserEntity.createNewUser(newUser);
        user.persistAndFlush();
        return user.getId();
    }

    public UserDataDTO getUserById(Long id){
        UserEntity user = UserEntity.findById(id);
        return new UserDataDTO(user.username);
    }

    @Transactional
    public void updateUsersData(FullUserDTO userData){
        UserEntity user = UserEntity.findById(userData.id);
        user.username = userData.username;
        user.password = BcryptUtil.bcryptHash(userData.password);
        user.role = userData.role;
    }

    public UserDTO getUserByUsername (String username){
        return new UserDTO(UserEntity.getByUsername(username));
    }


}
