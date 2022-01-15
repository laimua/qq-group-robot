package simbot.example.persistence.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import simbot.example.persistence.domain.KeyWordBan;
import simbot.example.persistence.domain.User;
import simbot.example.persistence.mapper.KeyWordBanMapper;
import simbot.example.persistence.mapper.UserMapper;
import simbot.example.persistence.service.IKeyWordBanService;
import simbot.example.persistence.service.IUserService;

import java.util.List;

@Service
public class KeyWordBanServiceImpl implements IKeyWordBanService {
    @Autowired
    private KeyWordBanMapper keyWordBanMapper;

    @Override
    public int insert(KeyWordBan word) {
        return keyWordBanMapper.insert(word);
    }

    @Override
    public List<KeyWordBan> selectKeyWordList(KeyWordBan word) {
        return keyWordBanMapper.selectKeyWordList(word);
    }
}
