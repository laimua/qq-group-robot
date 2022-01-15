package simbot.example.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import simbot.example.persistence.domain.User;

import java.util.List;

@Mapper
public interface UserMapper {

    int insert(User user);
    
    User selectUserByCode(String code);

    int updateUser(User user);

    List<User> selectUserList(User user);



}
