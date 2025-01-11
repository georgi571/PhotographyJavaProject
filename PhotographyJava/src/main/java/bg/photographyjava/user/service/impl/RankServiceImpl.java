package bg.photographyjava.user.service.impl;

import bg.photographyjava.user.model.Rank;
import bg.photographyjava.user.property.enums.UserRank;
import bg.photographyjava.user.repository.RankRepository;
import bg.photographyjava.user.service.RankService;
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
