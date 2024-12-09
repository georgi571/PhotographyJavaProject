package bg.photographyjava.service.impl;

import bg.photographyjava.model.entity.Rank;
import bg.photographyjava.model.enums.UserRank;
import bg.photographyjava.repository.RankRepository;
import bg.photographyjava.service.RankService;
import org.springframework.stereotype.Service;

@Service
public class RankServiceImpl implements RankService {

    private final RankRepository rankRepository;

    public RankServiceImpl(RankRepository rankRepository) {
        this.rankRepository = rankRepository;
    }

    @Override
    public void seedRanks() {
        if (this.rankRepository.count() == 0) {
            for (UserRank rank : UserRank.getRanks()) {
                this.rankRepository.saveAndFlush(new Rank(rank));
            }
        }
    }
}
