package simbot.example.persistence.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import simbot.example.persistence.domain.KeyWordBan;
import simbot.example.persistence.domain.PointLog;
import simbot.example.persistence.mapper.KeyWordBanMapper;
import simbot.example.persistence.mapper.PointLogMapper;
import simbot.example.persistence.service.IKeyWordBanService;
import simbot.example.persistence.service.IPointLogService;

import java.util.List;

@Service
public class PointLogServiceImpl implements IPointLogService {
    @Autowired
    private PointLogMapper pointLogMapper;


    @Override
    public int insertPointLog(PointLog log) {
        return pointLogMapper.insertPointLog(log);
    }

    @Override
    public List<KeyWordBan> selectKeyWordList(KeyWordBan word) {
        return null;
    }
}
