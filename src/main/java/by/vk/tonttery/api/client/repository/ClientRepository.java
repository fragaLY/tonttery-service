package by.vk.tonttery.api.client.repository;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * The client's repository.
 */
public interface ClientRepository extends PagingAndSortingRepository<Client, UUID>,
    CrudRepository<Client, UUID> {

  Page<Client> findAllByLotteriesId(UUID lotteryId, Pageable pageable);
}
