package simbot.example.persistence.service;


import simbot.example.persistence.domain.KeyWordBan;
import simbot.example.persistence.domain.User;

import java.util.List;

public interface IKeyWordBanService {
    int insert(KeyWordBan word);

    List<KeyWordBan> selectKeyWordList(KeyWordBan word);
}
