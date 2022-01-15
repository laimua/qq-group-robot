package simbot.example.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import simbot.example.persistence.domain.KeyWordBan;
import simbot.example.persistence.domain.PointLog;

import java.util.List;

@Mapper
public interface PointLogMapper {

    int insertPointLog(PointLog log);

    public List<KeyWordBan> selectKeyWordList(KeyWordBan word);

}
