package by.vk.tonttery.api.service;

import by.vk.tonttery.api.client.repository.Client;
import by.vk.tonttery.api.client.repository.ClientId;
import by.vk.tonttery.api.client.repository.ClientRepository;
import by.vk.tonttery.api.client.response.ClientResponse;
import by.vk.tonttery.api.client.response.ClientShortResponse;
import by.vk.tonttery.api.exception.NotFoundException;
import by.vk.tonttery.api.lottery.repository.Lottery;
import by.vk.tonttery.api.lottery.repository.LotteryRepository;
import by.vk.tonttery.api.lottery.repository.Status;
import by.vk.tonttery.api.lottery.repository.Type;
import by.vk.tonttery.api.lottery.response.LotteryResponse;
import by.vk.tonttery.api.lottery.response.LotteryShortResponse;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.random.RandomGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class Service {

  private static final RandomGenerator RANDOM_GENERATOR = RandomGenerator.getDefault();

  private final ClientRepository clientRepository;
  private final LotteryRepository lotteryRepository;
  private final CalculationService calculationService;

  @NotNull
  @Transactional
  @CachePut(cacheNames = "lotteries", key = "{ #result.id(), #result.winnerId() }")
  public LotteryResponse create(@NotNull Type type, @NotNull LocalDate now) {
    var lottery = new Lottery(type, type.nextLotteryDate(now));
    var createdLottery = lotteryRepository.save(lottery);
    return LotteryResponse.from(createdLottery, BigDecimal.ZERO);
  }

  @NotNull
  @Transactional
  @CachePut(cacheNames = "lotteries", key = "{ #result.id(), #result.winnerId() }")
  public LotteryResponse award(@NotNull Type type, @NotNull LocalDate startDate) {
    log.info("[TONTERRY] Searching a winner for a lottery with date [{}] and type [{}]", startDate,
        type);
    var lottery = lotteryRepository.findLotteryByTypeAndStatusAndStartDate(type, Status.CREATED,
        startDate).orElseThrow(() -> new NotFoundException(
        "Lottery with start date [%s] and type [%s] not found".formatted(startDate, type)));

    var playersIds = lottery.getClients().parallelStream().map(Client::getId).map(ClientId::getId)
        .toList();
    if (playersIds.isEmpty()) {
      return LotteryResponse.from(lottery, BigDecimal.ZERO);
    }

    Collections.shuffle(playersIds, RANDOM_GENERATOR);
    var winnerId = playersIds.parallelStream().skip(RANDOM_GENERATOR.nextInt(playersIds.size()))
        .findFirst().orElseThrow(() -> new NotFoundException(
            "The winner of lottery with id [%s] not found".formatted(lottery.getId())));

    var winner = clientRepository.findById(winnerId)
        .orElseThrow(() -> new NotFoundException(Client.class.getSimpleName(), winnerId));

    lottery.setWinner(winner);
    lottery.setStatus(Status.COMPLETED);
    lotteryRepository.save(lottery);
    var prize = calculationService.prize(playersIds.size());
    return LotteryResponse.from(lottery, winner, prize);
  }

  @NotNull
  @Transactional(readOnly = true)
  @Cacheable(cacheNames = "lotteries", key = "{ #lotteryId, #clientId }")
  public LotteryResponse lottery(@NotNull UUID lotteryId, @Nullable UUID clientId) {
    log.info("[TONTERRY] Searching a lottery with id [{}] for client [{}]", lotteryId, clientId);
    var lottery = lotteryRepository.findById(lotteryId)
        .orElseThrow(() -> new NotFoundException(Lottery.class.getSimpleName(), lotteryId));
    var clients = lottery.getClients();
    var winner = lottery.getWinner();
    var isClientJoined = clientId != null && clients.stream()
        .anyMatch(it -> it.getId().getId().equals(clientId));
    var prize = calculationService.prize(clients.size());
    return LotteryResponse.from(lottery, winner, prize, isClientJoined);
  }

  @NotNull
  @Transactional
  @Caching(
      put = {@CachePut(cacheNames = "lotteries", key = "{ #lotteryId, #clientId }")},
      evict = {
          @CacheEvict(cacheNames = "lotteryClients", allEntries = true),
          @CacheEvict(cacheNames = "clientLotteries", allEntries = true)
      }
  )
  public LotteryResponse join(@NotNull UUID lotteryId, @NotNull UUID clientId) {
    log.info("[TONTERRY] Joining a lottery with id [{}] by client [{}]", lotteryId, clientId);
    var client = clientRepository.findById(clientId)
        .orElseThrow(() -> new NotFoundException(Client.class.getSimpleName(), clientId));
    var lottery = lotteryRepository.findLotteryByIdAndStatus(lotteryId, Status.CREATED)
        .orElseThrow(() -> new NotFoundException(Lottery.class.getSimpleName(), lotteryId));
    var clients = lottery.getClients();
    clients.add(client);
    lottery.setClients(clients);
    lotteryRepository.save(lottery);
    var prize = calculationService.prize(clients.size());
    return LotteryResponse.from(lottery, lottery.getWinner(), prize, Boolean.TRUE);
  }

  @NotNull
  @Transactional
  @Caching(
      put = {@CachePut(cacheNames = "lotteries", key = "{ #lotteryId, #clientId }")},
      evict = {
          @CacheEvict(cacheNames = "lotteryClients", allEntries = true),
          @CacheEvict(cacheNames = "clientLotteries", allEntries = true)
      }
  )
  public LotteryResponse cancel(@NotNull UUID lotteryId, @NotNull UUID clientId) {
    log.info("[TONTERRY] Cancelling a lottery with id [{}] by client [{}]", lotteryId, clientId);
    var client = clientRepository.findById(clientId)
        .orElseThrow(() -> new NotFoundException(Client.class.getSimpleName(), clientId));
    var lottery = lotteryRepository.findById(lotteryId)
        .orElseThrow(() -> new NotFoundException(Lottery.class.getSimpleName(), lotteryId));
    var clients = lottery.getClients();
    clients.add(client);
    lottery.setClients(clients);
    lotteryRepository.save(lottery);
    var prize = calculationService.prize(clients.size());
    return LotteryResponse.from(lottery, lottery.getWinner(), prize, Boolean.FALSE);
  }

  @NotNull
  @Transactional(readOnly = true)
  @Cacheable(cacheNames = "clients", key = "#clientId")
  public ClientResponse client(@NotNull UUID clientId) {
    log.info("[TONTERRY] Searching a client with id [{}]", clientId);
    return clientRepository.findById(clientId).map(ClientResponse::from)
        .orElseThrow(() -> new NotFoundException(Client.class.getSimpleName(), clientId));
  }

  @NotNull
  @Transactional(readOnly = true)
  @Cacheable(cacheNames = "lotteryClients", key = "{ #lotteryId, #pageable }")
  public Page<ClientShortResponse> lotteryClients(@NotNull UUID lotteryId,
      @NotNull Pageable pageable) {
    log.info("[TONTERRY] Searching the clients of lottery with id [{}]", lotteryId);
    return clientRepository.findAllByLotteriesId(lotteryId, pageable)
        .map(ClientShortResponse::from);
  }

  @NotNull
  @Transactional(readOnly = true)
  @Cacheable(cacheNames = "clientLotteries", key = "{ #clientId, #pageable }")
  public Page<LotteryShortResponse> clientLotteries(@NotNull UUID clientId,
      @NotNull Pageable pageable) {
    log.info("[TONTERRY] Searching client's [{}] lotteries", clientId);
    var id = clientRepository.findById(clientId)
        .orElseThrow(() -> new NotFoundException(Client.class.getSimpleName(), clientId)).getId();
    return lotteryRepository.findByClientsId(id, pageable).map(LotteryShortResponse::from);
  }

  @NotNull
  @Transactional(readOnly = true)
  @Cacheable(cacheNames = "clientLotteries", key = "{ null, #pageable }")
  public Page<LotteryShortResponse> lotteries(@NotNull Pageable pageable) {
    log.info("[TONTERRY] Searching all lotteries");
    return lotteryRepository.findAll(pageable).map(LotteryShortResponse::from);
  }

  @NotNull
  public List<LotteryResponse> overview(@NotNull LocalDate startDate) {
    return lotteryRepository.findByGreaterThanEqualStarDate(startDate).stream().map(it -> {
      var clients = it.getClients();
      var winner = it.getWinner();
      var prize = calculationService.prize(clients.size());
      return LotteryResponse.from(it, winner, prize);
    }).toList();
  }
}
