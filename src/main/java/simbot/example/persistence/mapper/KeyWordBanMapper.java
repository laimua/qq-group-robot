package simbot.example.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import simbot.example.persistence.domain.KeyWordBan;
import simbot.example.persistence.domain.User;

import java.util.List;

@Mapper
public interface KeyWordBanMapper {

    int insert(KeyWordBan word);

    public List<KeyWordBan> selectKeyWordList(KeyWordBan word);

}
