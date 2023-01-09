package org.ai_multiuser_game.services;

import org.ai_multiuser_game.data.FullUserDTO;
import org.ai_multiuser_game.data.StartupUserDTO;
import org.ai_multiuser_game.data.UserDTO;
import org.ai_multiuser_game.data.UserDataDTO;
import org.ai_multiuser_game.entities.UserEntity;

import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.util.List;

@Singleton
public class UsersService {

    public List<FullUserDTO> getUsers(){
        return UserEntity.listAll().stream().map(e -> new FullUserDTO((UserEntity) e)).toList();
    }

    @Transactional
    public Long createUser(StartupUserDTO newUser){
        var user = new UserEntity(newUser);
        return user.id;
    }

    public UserDataDTO getUserById(Long id){
        UserEntity user = UserEntity.findById(id);
        return new UserDataDTO(user.username);
    }

    @Transactional
    public void updateUsersData(FullUserDTO userData){
        UserEntity user = UserEntity.findById(userData.id);
        user.password = userData.password;
        user.username = userData.username;
        user.password = userData.password;
        user.role = userData.role;
    }

    public UserDTO getUserByUsername (String username){
        return new UserDTO(UserEntity.getByUsername(username));
    }


}
