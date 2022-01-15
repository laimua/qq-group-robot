package simbot.example.persistence.service;


import simbot.example.persistence.domain.KeyWordBan;
import simbot.example.persistence.domain.PointLog;

import java.util.List;

public interface IPointLogService {
    int insertPointLog(PointLog log);

    List<KeyWordBan> selectKeyWordList(KeyWordBan word);
}
