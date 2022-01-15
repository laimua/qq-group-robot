package simbot.example.persistence.service;


import simbot.example.persistence.domain.User;

import java.util.List;

public interface IUserService {
    int insert(User user);

    User selectUserByCode(String code);

    int updateUser(User user);

    List<User> selectUserList(User user);

}
