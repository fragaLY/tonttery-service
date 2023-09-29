package by.vk.tonttery.api.lottery.repository;

import by.vk.tonttery.api.client.repository.ClientId;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * The repository of lottery.
 */
public interface LotteryRepository extends PagingAndSortingRepository<Lottery, UUID>,
    CrudRepository<Lottery, UUID> {

  Page<Lottery> findByClientsId(ClientId clientId, Pageable pageable);

  Optional<Lottery> findLotteryByIdAndStatus(UUID id, Status status);

  Optional<Lottery> findLotteryByTypeAndStatusAndStartDate(Type type, Status status, LocalDate startDate);

  List<Lottery> findByGreaterThanEqualStarDate(LocalDate startDate);

}
