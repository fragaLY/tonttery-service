/**
 * Copyright © 2023-2024 Vadzim Kavalkou. All Rights Reserved. All information contained herein is,
 * and remains the property of Vadzim Kavalkou and/or its suppliers and is protected by
 * international intellectual property law. Dissemination of this information or reproduction of
 * this material is strictly forbidden, unless prior written permission is obtained from Vadzim
 * Kavalkou.
 **/

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
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
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

/**
 * The service for the lottery.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TonterryService {

  private static final RandomGenerator RANDOM_GENERATOR = RandomGenerator.getDefault();

  private final ClientRepository clientRepository;
  private final LotteryRepository lotteryRepository;
  private final CalculationService calculationService;

  /**
   * Creates a new lottery.
   *
   * @param type         - the type of lottery. Should not be null.
   * @param creationDate - the date of creation the lottery. Should not be null.
   *
   * @return the created lottery. Should not be null.
   */
  @NotNull
  @Transactional
  @CachePut(cacheNames = "lotteries", key = "{ #result.id(), #result.winnerId() }")
  public LotteryResponse create(
      @NotNull(message = "The type of lottery should not be null") Type type,
      @PastOrPresent(message = "The date of creation the lottery should be in past or present") LocalDate creationDate) {
    var lottery = new Lottery(type, type.nextLotteryDate(creationDate));
    var createdLottery = lotteryRepository.save(lottery);
    return LotteryResponse.from(createdLottery, BigDecimal.ZERO);
  }

  /**
   * Awards a lottery using randomizing, shuffling and parallel streams to enforce the natural
   * ordering breaking.
   *
   * @param type      - the type of lottery. Should not be null.
   * @param startDate - the date of creation the lottery. Should be in past of present.
   *
   * @return the awarded lottery. Should not be null.
   *
   * @throws NotFoundException if the winner or lottery were not found.
   */
  @NotNull
  @Transactional
  @CachePut(cacheNames = "lotteries", key = "{ #result.id(), #result.winnerId() }")
  public LotteryResponse award(
      @NotNull(message = "The type of lottery should not be null") Type type,
      @PastOrPresent(message = "The awarding lottery date should be in past or present") LocalDate startDate) {
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

  /**
   * Retrieves a lottery by its id for client. If client is not presenting in the lottery it will
   * set the flag.
   *
   * @param lotteryId - the id of lottery. Should not be null.
   * @param clientId  - the id of client. Can be null.
   *
   * @return the lottery. Should not be null.
   */
  @NotNull
  @Transactional(readOnly = true)
  @Cacheable(cacheNames = "lotteries", key = "{ #lotteryId, #clientId }")
  public LotteryResponse lottery(
      @NotNull(message = "The id of lottery should not be null") UUID lotteryId,
      @Nullable UUID clientId) {
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

  /**
   * Joining a lottery by client.
   *
   * @param lotteryId - the id of lottery. Should not be null.
   * @param clientId  - the id of client. Should not be null.
   *
   * @return the joined lottery. Should not be null.
   *
   * @throws NotFoundException if the client or lottery were not found.
   */
  @NotNull
  @Transactional
  @Caching(
      put = {@CachePut(cacheNames = "lotteries", key = "{ #lotteryId, #clientId }")},
      evict = {
          @CacheEvict(cacheNames = "lotteryClients", allEntries = true),
          @CacheEvict(cacheNames = "clientLotteries", allEntries = true)
      }
  )
  public LotteryResponse join(
      @NotNull(message = "The id of lottery should not be null") UUID lotteryId,
      @NotNull(message = "The client's id should not be null") UUID clientId) {
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

  /**
   * Cancels a lottery payment for a client.
   *
   * @param lotteryId - the id of lottery. Should not be null.
   * @param clientId  - the id of client. Should not be null.
   *
   * @return the cancelled lottery. Should not be null.
   *
   * @throws NotFoundException if the client or lottery were not found.
   */
  @NotNull
  @Transactional
  @Caching(
      put = {@CachePut(cacheNames = "lotteries", key = "{ #lotteryId, #clientId }")},
      evict = {
          @CacheEvict(cacheNames = "lotteryClients", allEntries = true),
          @CacheEvict(cacheNames = "clientLotteries", allEntries = true)
      }
  )
  public LotteryResponse cancel(
      @NotNull(message = "The id of lottery should not be null") UUID lotteryId,
      @NotNull(message = "The client's id should not be null") UUID clientId) {
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

  /**
   * Retrieves a client by client's internal id.
   *
   * @param clientId - the id of client. Should not be null.
   *
   * @return the client. Should not be null.
   *
   * @throws NotFoundException if the client was not found.
   */
  @NotNull
  @Transactional(readOnly = true)
  @Cacheable(cacheNames = "clients", key = "#clientId")
  public ClientResponse client(
      @NotNull(message = "The client's id should not be null") UUID clientId) {
    log.info("[TONTERRY] Searching a client with id [{}]", clientId);
    return clientRepository.findById(clientId).map(ClientResponse::from)
        .orElseThrow(() -> new NotFoundException(Client.class.getSimpleName(), clientId));
  }

  /**
   * Retrieves the lottery clients.
   *
   * @param lotteryId - the id of lottery. Should not be null.
   * @param pageable  - the pagination configuration. Should not be null.
   *
   * @return the page of lottery clients. Should not be null.
   */
  @NotNull
  @Transactional(readOnly = true)
  @Cacheable(cacheNames = "lotteryClients", key = "{ #lotteryId, #pageable }")
  public Page<ClientShortResponse> lotteryClients(
      @NotNull(message = "The id of lottery should not be null") UUID lotteryId,
      @NotNull(message = "The pagination configuration should not be null") Pageable pageable) {
    log.info("[TONTERRY] Searching the clients of lottery with id [{}]", lotteryId);
    return clientRepository.findAllByLotteriesId(lotteryId, pageable)
        .map(ClientShortResponse::from);
  }

  /**
   * Retrieves the client's lotteries.
   *
   * @param clientId - the id of client. Should not be null.
   * @param pageable - the pagination configuration. Should not be null.
   *
   * @return the page of client's lotteries. Should not be null.
   *
   * @throws NotFoundException if the client was not found.
   */
  @NotNull
  @Transactional(readOnly = true)
  @Cacheable(cacheNames = "clientLotteries", key = "{ #clientId, #pageable }")
  public Page<LotteryShortResponse> clientLotteries(
      @NotNull(message = "The client's id should not be null") UUID clientId,
      @NotNull(message = "The pagination configuration should not be null") Pageable pageable) {
    log.info("[TONTERRY] Searching client's [{}] lotteries", clientId);
    var id = clientRepository.findById(clientId)
        .orElseThrow(() -> new NotFoundException(Client.class.getSimpleName(), clientId)).getId();
    return lotteryRepository.findByClientsId(id, pageable).map(LotteryShortResponse::from);
  }

  /**
   * Retrieves all lotteries.
   *
   * @param pageable - the pagination configuration. Should not be null.
   *
   * @return the page of all lotteries. Should not be null.
   */
  @NotNull
  @Transactional(readOnly = true)
  @Cacheable(cacheNames = "clientLotteries", key = "{ null, #pageable }")
  public Page<LotteryShortResponse> lotteries(
      @NotNull(message = "The pagination configuration should not be null") Pageable pageable) {
    log.info("[TONTERRY] Searching all lotteries");
    return lotteryRepository.findAll(pageable).map(LotteryShortResponse::from);
  }

  /**
   * Overview of the upcoming lotteries.
   *
   * @param startDate - the date of start. Should not be null.
   *
   * @return the list of upcoming lotteries. Should not be empty.
   */
  @NotEmpty(message = "The amount of upcoming lotteries should not be zero")
  public List<LotteryResponse> overview(
      @FutureOrPresent(message = "The date for upcoming lotteries should be at least in present day") LocalDate startDate) {
    log.info("[TONTERRY] Overview of the upcoming lotteries");
    return lotteryRepository.findByGreaterThanEqualStarDate(startDate).stream().map(it -> {
      var clients = it.getClients();
      var winner = it.getWinner();
      var prize = calculationService.prize(clients.size());
      return LotteryResponse.from(it, winner, prize);
    }).toList();
  }
}
