package by.vk.tonttery.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import by.vk.tonttery.TestObjects;
import by.vk.tonttery.api.client.repository.Client;
import by.vk.tonttery.api.client.repository.ClientId;
import by.vk.tonttery.api.client.repository.ClientRepository;
import by.vk.tonttery.api.client.response.ClientResponse;
import by.vk.tonttery.api.client.response.ClientShortResponse;
import by.vk.tonttery.api.exception.BadRequestException;
import by.vk.tonttery.api.exception.NotFoundException;
import by.vk.tonttery.api.lottery.repository.LotteryRepository;
import by.vk.tonttery.api.lottery.repository.Status;
import by.vk.tonttery.api.lottery.repository.Type;
import by.vk.tonttery.api.lottery.response.LotteryResponse;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;


@Tags({@Tag("unit"), @Tag("tonttery")})
class TonterryServiceTest {

  @Mock
  ClientRepository clientRepository;
  @Mock
  LotteryRepository lotteryRepository;

  @Mock
  CalculationService calculationService;

  TonterryService service;

  @BeforeEach
  void setUp() {
    service = new TonterryService(clientRepository, lotteryRepository, calculationService);
  }

  @Test
  @DisplayName("Creation of the daily lottery")
  void createDaily() {
    //given
    var type = Type.DAILY;
    var date = LocalDate.of(2023, 10, 2);
    var startDate = date.plusDays(1L);
    var lotteryId = UUID.randomUUID();
    var lottery = TestObjects.lottery(lotteryId, type, startDate);
    var expected = TestObjects.lotteryResponse(lotteryId, type, startDate);
    when(lotteryRepository.save(lottery)).thenReturn(lottery);

    //when
    var actual = service.create(type, date);

    //then
    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Creation of the weekly lottery")
  void createWeekly() {
    //given
    var type = Type.WEEKLY;
    var date = LocalDate.of(2023, 10, 2);
    var startDate = date.plusWeeks(1L).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    var lotteryId = UUID.randomUUID();
    var lottery = TestObjects.lottery(lotteryId, type, startDate);
    var expected = TestObjects.lotteryResponse(lotteryId, type, startDate);
    when(lotteryRepository.save(lottery)).thenReturn(lottery);

    //when
    var actual = service.create(type, date);

    //then
    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Creation of the monthly lottery")
  void createMonthly() {
    //given
    var type = Type.MONTHLY;
    var date = LocalDate.of(2023, 10, 1);
    var startDate = date.plusMonths(1L).with(TemporalAdjusters.firstDayOfMonth());
    var lotteryId = UUID.randomUUID();
    var lottery = TestObjects.lottery(lotteryId, type, startDate);
    var expected = TestObjects.lotteryResponse(lotteryId, type, startDate);
    when(lotteryRepository.save(lottery)).thenReturn(lottery);

    //when
    var actual = service.create(type, date);

    //then
    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Creation of the yearly lottery")
  void createYearly() {
    var type = Type.YEARLY;
    var date = LocalDate.of(2023, 1, 1);
    var startDate = date.plusYears(1L).with(TemporalAdjusters.firstDayOfYear());
    var lotteryId = UUID.randomUUID();
    var lottery = TestObjects.lottery(lotteryId, type, startDate);
    var expected = TestObjects.lotteryResponse(lotteryId, type, startDate);
    when(lotteryRepository.save(lottery)).thenReturn(lottery);

    //when
    var actual = service.create(type, date);

    //then
    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Awarding not existing lottery")
  void awardWhenLotteryNotFound() {
    //given
    var type = Type.DAILY;
    var startDate = LocalDate.of(2023, 10, 2);
    when(lotteryRepository.findLotteryByTypeAndStatusAndStartDate(type, Status.CREATED,
        startDate)).thenReturn(Optional.empty());

    //when-then
    assertThrows(NotFoundException.class, () -> service.award(type, startDate));
  }

  @Test
  @DisplayName("Awarding a lottery with already existing winner")
  void awardWhenWinnerAlreadyExists() {
    //given
    var type = Type.DAILY;
    var startDate = LocalDate.of(2023, 10, 2);
    var lotteryId = UUID.randomUUID();
    var lottery = TestObjects.lottery(lotteryId, type, startDate);
    var winner = new Client(new ClientId(UUID.randomUUID(), 1L), "Vadzim", "Kavalkou", "vk", false,
        true, "shortenUrl", LocalDateTime.now(), null, LocalDateTime.now(), LocalDateTime.now());
    lottery.setWinner(winner);
    when(lotteryRepository.findLotteryByTypeAndStatusAndStartDate(type, Status.CREATED,
        startDate)).thenReturn(Optional.of(lottery));

    //when-then
    assertThrows(BadRequestException.class, () -> service.award(type, startDate));
  }

  @Test
  @DisplayName("Awarding a daily lottery with null players")
  void awardWhenNullPlayers() {
    //given
    var type = Type.DAILY;
    var startDate = LocalDate.of(2023, 10, 2);
    var lotteryId = UUID.randomUUID();
    var lottery = TestObjects.lottery(lotteryId, type, startDate);
    var expected = new LotteryResponse(lottery.getId(), null, null, lottery.getType(),
        lottery.getStatus(), lottery.getStartDate(), lottery.getClients().size(), BigDecimal.ZERO,
        false);
    when(lotteryRepository.findLotteryByTypeAndStatusAndStartDate(type, Status.CREATED,
        startDate)).thenReturn(Optional.of(lottery));

    //when
    var actual = service.award(Type.DAILY, startDate);

    //then
    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Awarding a daily lottery with no players")
  void awardWhenNoPlayers() {
    //given
    var type = Type.DAILY;
    var startDate = LocalDate.of(2023, 10, 2);
    var lotteryId = UUID.randomUUID();
    var lottery = TestObjects.lottery(lotteryId, type, startDate);
    lottery.setClients(Set.of());
    var expected = new LotteryResponse(lottery.getId(), null, null, lottery.getType(),
        lottery.getStatus(), lottery.getStartDate(), lottery.getClients().size(), BigDecimal.ZERO,
        false);
    when(lotteryRepository.findLotteryByTypeAndStatusAndStartDate(type, Status.CREATED,
        startDate)).thenReturn(Optional.of(lottery));

    //when
    var actual = service.award(Type.DAILY, startDate);

    //then
    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Awarding a daily lottery with one player")
  void awardDailyLotteryWithOnePlayer() {
    //given
    var type = Type.DAILY;
    var startDate = LocalDate.of(2023, 10, 2);
    var lotteryId = UUID.randomUUID();
    var lottery = TestObjects.lottery(lotteryId, type, startDate);
    var expected = new LotteryResponse(lottery.getId(), null, null, lottery.getType(),
        lottery.getStatus(), lottery.getStartDate(), lottery.getClients().size(), BigDecimal.ZERO,
        false);
    var firstClient = TestObjects.client(UUID.randomUUID(), 1L, "VK1");
    var clients = Set.of(firstClient);
    lottery.setClients(clients);
    when(lotteryRepository.findLotteryByTypeAndStatusAndStartDate(type, Status.CREATED,
        startDate)).thenReturn(Optional.of(lottery));

    //when
    var actual = service.award(Type.DAILY, startDate);

    //then
    assertEquals(expected, actual);
  }


  @Test
  @DisplayName("Awarding a daily lottery when winner doesnt exists")
  void awardDailyLotteryWhenWinnerDoesntExists() {
    //given
    var type = Type.DAILY;
    var startDate = LocalDate.of(2023, 10, 2);
    var lotteryId = UUID.randomUUID();
    var lottery = TestObjects.lottery(lotteryId, type, startDate);
    var firstClient = TestObjects.client(UUID.randomUUID(), 1L, "VK1");
    var secondClient = TestObjects.client(UUID.randomUUID(), 2L, "VK2");
    var clients = Set.of(firstClient, secondClient);
    lottery.setClients(clients);
    when(lotteryRepository.findLotteryByTypeAndStatusAndStartDate(type, Status.CREATED,
        startDate)).thenReturn(Optional.of(lottery));
    when(clientRepository.findById(any())).thenReturn(Optional.empty());

    //when-then
    assertThrows(NotFoundException.class, () -> service.award(Type.DAILY, startDate));
  }

  @Test
  @DisplayName("Awarding a daily lottery")
  void awardDailyLottery() {
    //given
    var type = Type.DAILY;
    var startDate = LocalDate.of(2023, 10, 2);
    var lotteryId = UUID.randomUUID();
    var lottery = TestObjects.lottery(lotteryId, type, startDate);
    var firstClient = TestObjects.client(UUID.randomUUID(), 1L, "VK1");
    var secondClient = TestObjects.client(UUID.randomUUID(), 2L, "VK2");
    var clients = Set.of(firstClient, secondClient);
    var prize = BigDecimal.valueOf(1.98d);
    lottery.setClients(clients);
    when(lotteryRepository.findLotteryByTypeAndStatusAndStartDate(type, Status.CREATED,
        startDate)).thenReturn(Optional.of(lottery));
    when(clientRepository.findById(any())).thenReturn(Optional.of(firstClient));
    lottery.setWinner(firstClient);
    lottery.setStatus(Status.COMPLETED);
    doNothing().when(lotteryRepository).save(lottery);
    when(calculationService.prize(clients.size())).thenReturn(prize);
    var expected = new LotteryResponse(lottery.getId(), firstClient.getId().getId(), "VK1",
        lottery.getType(), lottery.getStatus(), lottery.getStartDate(), lottery.getClients().size(),
        prize, false);

    //when
    var actual = service.award(Type.DAILY, startDate);

    //then
    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Awarding a weekly lottery")
  void awardWeeklyLottery() {
    //given
    var type = Type.WEEKLY;
    var startDate = LocalDate.of(2023, 10, 1);
    var lotteryId = UUID.randomUUID();
    var lottery = TestObjects.lottery(lotteryId, type, startDate);
    var firstClient = TestObjects.client(UUID.randomUUID(), 1L, "VK1");
    var secondClient = TestObjects.client(UUID.randomUUID(), 2L, "VK2");
    var clients = Set.of(firstClient, secondClient);
    var prize = BigDecimal.valueOf(1.98d);
    lottery.setClients(clients);
    when(lotteryRepository.findLotteryByTypeAndStatusAndStartDate(type, Status.CREATED,
        startDate)).thenReturn(Optional.of(lottery));
    when(clientRepository.findById(any())).thenReturn(Optional.of(firstClient));
    lottery.setWinner(firstClient);
    lottery.setStatus(Status.COMPLETED);
    doNothing().when(lotteryRepository).save(lottery);
    when(calculationService.prize(clients.size())).thenReturn(prize);
    var expected = new LotteryResponse(lottery.getId(), firstClient.getId().getId(), "VK1",
        lottery.getType(), lottery.getStatus(), lottery.getStartDate(), lottery.getClients().size(),
        prize, false);

    //when
    var actual = service.award(Type.WEEKLY, startDate);

    //then
    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Awarding a monthly lottery")
  void awardMonthlyLottery() {
    //given
    var type = Type.MONTHLY;
    var startDate = LocalDate.of(2023, 10, 1);
    var lotteryId = UUID.randomUUID();
    var lottery = TestObjects.lottery(lotteryId, type, startDate);
    var firstClient = TestObjects.client(UUID.randomUUID(), 1L, "VK1");
    var secondClient = TestObjects.client(UUID.randomUUID(), 2L, "VK2");
    var clients = Set.of(firstClient, secondClient);
    var prize = BigDecimal.valueOf(1.98d);
    lottery.setClients(clients);
    when(lotteryRepository.findLotteryByTypeAndStatusAndStartDate(type, Status.CREATED,
        startDate)).thenReturn(Optional.of(lottery));
    when(clientRepository.findById(any())).thenReturn(Optional.of(firstClient));
    lottery.setWinner(firstClient);
    lottery.setStatus(Status.COMPLETED);
    doNothing().when(lotteryRepository).save(lottery);
    when(calculationService.prize(clients.size())).thenReturn(prize);
    var expected = new LotteryResponse(lottery.getId(), firstClient.getId().getId(), "VK1",
        lottery.getType(), lottery.getStatus(), lottery.getStartDate(), lottery.getClients().size(),
        prize, false);

    //when
    var actual = service.award(Type.MONTHLY, startDate);

    //then
    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Awarding a yearly lottery")
  void awardYearlyLottery() {
    //given
    var type = Type.YEARLY;
    var startDate = LocalDate.of(2023, 10, 1);
    var lotteryId = UUID.randomUUID();
    var lottery = TestObjects.lottery(lotteryId, type, startDate);
    var firstClient = TestObjects.client(UUID.randomUUID(), 1L, "VK1");
    var secondClient = TestObjects.client(UUID.randomUUID(), 2L, "VK2");
    var clients = Set.of(firstClient, secondClient);
    var prize = BigDecimal.valueOf(1.98d);
    lottery.setClients(clients);
    when(lotteryRepository.findLotteryByTypeAndStatusAndStartDate(type, Status.CREATED,
        startDate)).thenReturn(Optional.of(lottery));
    when(clientRepository.findById(any())).thenReturn(Optional.of(firstClient));
    lottery.setWinner(firstClient);
    lottery.setStatus(Status.COMPLETED);
    doNothing().when(lotteryRepository).save(lottery);
    when(calculationService.prize(clients.size())).thenReturn(prize);
    var expected = new LotteryResponse(lottery.getId(), firstClient.getId().getId(), "VK1",
        lottery.getType(), lottery.getStatus(), lottery.getStartDate(), lottery.getClients().size(),
        prize, false);

    //when
    var actual = service.award(Type.YEARLY, startDate);

    //then
    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Getting a lottery by id. Lottery not found.")
  void lotteryWhenLotteryNotFound() {
    //given
    var clientId = UUID.randomUUID();
    var lotteryId = UUID.randomUUID();
    when(lotteryRepository.findById(lotteryId)).thenReturn(Optional.empty());

    //when-then
    assertThrows(NotFoundException.class, () -> service.lottery(lotteryId, clientId));
  }

  @Test
  @DisplayName("Getting a lottery by id. Client was not in lottery.")
  void lotteryWhenClientNotAlreadyJoinedLottery() {
    //given
    var clientId = UUID.randomUUID();
    var lotteryId = UUID.randomUUID();
    var startDate = LocalDate.now();
    var lottery = TestObjects.lottery(lotteryId, Type.DAILY, startDate);
    var anotherClient = TestObjects.client(UUID.randomUUID(), 1L, "VK1");
    var clients = Set.of(anotherClient);
    lottery.setClients(clients);
    when(lotteryRepository.findById(lotteryId)).thenReturn(Optional.of(lottery));
    when(calculationService.prize(1)).thenReturn(BigDecimal.valueOf(0.99d));
    var expected = new LotteryResponse(lotteryId, null, null, Type.DAILY, Status.CREATED, startDate,
        1, BigDecimal.valueOf(0.99d), false);

    //when-then
    assertEquals(expected, service.lottery(lotteryId, clientId));
  }

  @Test
  @DisplayName("Getting a lottery by id. Client was in lottery.")
  void lotteryWhenClientAlreadyJoinedLottery() {
    //given
    var clientId = UUID.randomUUID();
    var lotteryId = UUID.randomUUID();
    var startDate = LocalDate.now();
    var lottery = TestObjects.lottery(lotteryId, Type.DAILY, startDate);
    var client = TestObjects.client(clientId, 1L, "VK1");
    var anotherClient = TestObjects.client(UUID.randomUUID(), 2L, "VK2");
    var clients = Set.of(client, anotherClient);
    lottery.setClients(clients);
    when(lotteryRepository.findById(lotteryId)).thenReturn(Optional.of(lottery));
    when(calculationService.prize(1)).thenReturn(BigDecimal.valueOf(1.98d));
    var expected = new LotteryResponse(lotteryId, null, null, Type.DAILY, Status.CREATED, startDate,
        2, BigDecimal.valueOf(1.98d), true);

    //when-then
    assertEquals(expected, service.lottery(lotteryId, clientId));
  }

  @Test
  @DisplayName("Joining a lottery. Client was not found.")
  void joinWhenClientNotFound() {
    //given
    var lotteryId = UUID.randomUUID();
    var clientId = UUID.randomUUID();
    when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

    //when-then
    assertThrows(NotFoundException.class, () -> service.join(lotteryId, clientId));
  }

  @Test
  @DisplayName("Joining a lottery. Lottery was not found.")
  void joinWhenLotteryNotFound() {
    //given
    var lotteryId = UUID.randomUUID();
    var clientId = UUID.randomUUID();
    when(clientRepository.findById(clientId)).thenReturn(Optional.of(new Client()));
    when(lotteryRepository.findLotteryByIdAndStatus(lotteryId, Status.CREATED)).thenReturn(
        Optional.empty());

    //when-then
    assertThrows(NotFoundException.class, () -> service.join(lotteryId, clientId));
  }

  @Test
  @DisplayName("Joining a lottery. Client was in lottery.")
  void joinWhenClientWasInLottery() {
    //given
    var lotteryId = UUID.randomUUID();
    var clientId = UUID.randomUUID();
    var startDate = LocalDate.now();
    var client = TestObjects.client(clientId, 1L, "VK1");
    var anotherClient = TestObjects.client(clientId, 2L, "VK2");
    var lottery = TestObjects.lottery(lotteryId, Type.DAILY, startDate);
    var clients = Set.of(client, anotherClient);
    lottery.setClients(clients);
    when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
    when(lotteryRepository.findLotteryByIdAndStatus(lotteryId, Status.CREATED)).thenReturn(
        Optional.of(lottery));
    when(calculationService.prize(2)).thenReturn(BigDecimal.valueOf(1.98d));
    var expected = new LotteryResponse(lotteryId, null, null, Type.DAILY, Status.CREATED, startDate,
        2, BigDecimal.valueOf(1.98d), true);

    //when
    var actual = service.join(lotteryId, clientId);

    //then
    assertEquals(expected, actual);
    verify(lotteryRepository, never()).save(lottery);
  }

  @Test
  @DisplayName("Joining a lottery. Client wasn't in lottery.")
  void joinWhenClientWasNotInLottery() {
    //given
    var lotteryId = UUID.randomUUID();
    var clientId = UUID.randomUUID();
    var startDate = LocalDate.now();
    var client = TestObjects.client(clientId, 1L, "VK1");
    var anotherClient = TestObjects.client(clientId, 2L, "VK2");
    var lottery = TestObjects.lottery(lotteryId, Type.DAILY, startDate);
    var clients = Set.of(anotherClient);
    lottery.setClients(clients);
    when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
    when(lotteryRepository.findLotteryByIdAndStatus(lotteryId, Status.CREATED)).thenReturn(
        Optional.of(lottery));
    when(lotteryRepository.save(lottery)).thenReturn(lottery);
    when(calculationService.prize(2)).thenReturn(BigDecimal.valueOf(1.98d));
    var expected = new LotteryResponse(lotteryId, null, null, Type.DAILY, Status.CREATED, startDate,
        2, BigDecimal.valueOf(1.98d), true);

    //when
    var actual = service.join(lotteryId, clientId);

    //then
    assertEquals(expected, actual);
    verify(lotteryRepository, only()).save(lottery);
  }

  @Test
  @DisplayName("Cancelling a lottery participation. Client was not found.")
  void cancelWhenClientNotFound() {
    //given
    var lotteryId = UUID.randomUUID();
    var clientId = UUID.randomUUID();
    when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

    //when-then
    assertThrows(NotFoundException.class, () -> service.cancel(lotteryId, clientId));
  }

  @Test
  @DisplayName("Cancelling a lottery participation. Lottery was not found.")
  void cancelWhenLotteryNotFound() {
    //given
    var lotteryId = UUID.randomUUID();
    var clientId = UUID.randomUUID();
    when(clientRepository.findById(clientId)).thenReturn(Optional.of(new Client()));
    when(lotteryRepository.findLotteryByIdAndStatus(lotteryId, Status.CREATED)).thenReturn(
        Optional.empty());

    //when-then
    assertThrows(NotFoundException.class, () -> service.cancel(lotteryId, clientId));
  }

  @Test
  @DisplayName("Cancelling a lottery participation. Client was not in lottery.")
  void cancelWhenClientWasNotInLottery() {
    //given
    var lotteryId = UUID.randomUUID();
    var clientId = UUID.randomUUID();
    var startDate = LocalDate.now();
    var client = TestObjects.client(clientId, 1L, "VK1");
    var anotherClient = TestObjects.client(clientId, 2L, "VK2");
    var lottery = TestObjects.lottery(lotteryId, Type.DAILY, startDate);
    var clients = Set.of(anotherClient);
    lottery.setClients(clients);
    when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
    when(lotteryRepository.findLotteryByIdAndStatus(lotteryId, Status.CREATED)).thenReturn(
        Optional.of(lottery));
    when(calculationService.prize(1)).thenReturn(BigDecimal.valueOf(0.99d));
    var expected = new LotteryResponse(lotteryId, null, null, Type.DAILY, Status.CREATED, startDate,
        1, BigDecimal.valueOf(0.99d), false);

    //when
    var actual = service.join(lotteryId, clientId);

    //then
    assertEquals(expected, actual);
    verify(lotteryRepository, never()).save(lottery);
  }

  @Test
  @DisplayName("Cancelling a lottery participation. Client was in lottery.")
  void cancelWhenClientWasInLottery() {
    //given
    var lotteryId = UUID.randomUUID();
    var clientId = UUID.randomUUID();
    var startDate = LocalDate.now();
    var client = TestObjects.client(clientId, 1L, "VK1");
    var anotherClient = TestObjects.client(clientId, 2L, "VK2");
    var lottery = TestObjects.lottery(lotteryId, Type.DAILY, startDate);
    var clients = Set.of(client, anotherClient);
    lottery.setClients(clients);
    when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
    when(lotteryRepository.findLotteryByIdAndStatus(lotteryId, Status.CREATED)).thenReturn(
        Optional.of(lottery));
    when(calculationService.prize(1)).thenReturn(BigDecimal.valueOf(0.99d));
    var expected = new LotteryResponse(lotteryId, null, null, Type.DAILY, Status.CREATED, startDate,
        1, BigDecimal.valueOf(0.99d), false);

    //when
    var actual = service.join(lotteryId, clientId);

    //then
    assertEquals(expected, actual);
    verify(lotteryRepository, only()).save(lottery);
  }

  @Test
  @DisplayName("Getting client when client not found.")
  void clientWhenClientNotFound() {
    //given
    var clientId = UUID.randomUUID();
    when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

    //when-then
    assertThrows(NotFoundException.class, () -> service.client(clientId));
  }

  @Test
  @DisplayName("Getting client when client found.")
  void client() {
    //given
    var clientId = UUID.randomUUID();
    var authenticatedAt = LocalDateTime.now();
    var lotteryId = UUID.randomUUID();
    var startDate = LocalDate.of(2023, 10, 2);
    var lotteries = Set.of(TestObjects.lottery(lotteryId, Type.DAILY, startDate));
    var client = new Client(new ClientId(clientId, 1L), "V", "K", "TG_NAME", true, false,
        "imageUrl", authenticatedAt, lotteries, LocalDateTime.now(), LocalDateTime.now());
    when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
    var expected = new ClientResponse(clientId, "V K", 1L, "TG_NAME", "imageUrl", authenticatedAt);

    //when
    var actual = service.client(clientId);

    //then
    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Getting lottery clients when clients exist. Page size is 2.")
  void lotteryClientsWhenExistPageOfTwo() {
    //given
    var pageable = Pageable.ofSize(2);
    var lotteryId = UUID.randomUUID();
    var clientId = UUID.randomUUID();
    var anotherClientId = UUID.randomUUID();

    var client = TestObjects.client(clientId, 1L, "VK1");
    var anotherClient = TestObjects.client(anotherClientId, 2L, "VK2");
    var clients = List.of(client, anotherClient);
    var pageOfEntity = new PageImpl<>(clients, pageable, clients.size());

    when(clientRepository.findAllByLotteriesId(lotteryId, pageable)).thenReturn(pageOfEntity);
    var firstClientResponse = new ClientShortResponse(clientId, "Vadzim Kavalkou", "VK1");
    var secondClientResponse = new ClientShortResponse(anotherClientId, "Vadzim Kavalkou", "VK2");
    var clientsResponses = List.of(firstClientResponse, secondClientResponse);
    var expected = new PageImpl<>(clientsResponses, pageable, clientsResponses.size());

    //when
    var actual = service.lotteryClients(lotteryId, pageable);

    //then
    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Getting lottery clients when clients exist. Page size is 1.")
  void lotteryClientsWhenExistPageOfOne() {
    //given
    var pageable = Pageable.ofSize(1);
    var lotteryId = UUID.randomUUID();
    var clientId = UUID.randomUUID();
    var anotherClientId = UUID.randomUUID();

    var client = TestObjects.client(clientId, 1L, "VK1");
    var anotherClient = TestObjects.client(anotherClientId, 2L, "VK2");
    var clients = List.of(client, anotherClient);
    var pageOfEntity = new PageImpl<>(clients, pageable, clients.size());

    when(clientRepository.findAllByLotteriesId(lotteryId, pageable)).thenReturn(pageOfEntity);
    var firstClientResponse = new ClientShortResponse(clientId, "Vadzim Kavalkou", "VK1");
    var secondClientResponse = new ClientShortResponse(anotherClientId, "Vadzim Kavalkou", "VK2");
    var clientsResponses = List.of(firstClientResponse, secondClientResponse);
    var expected = new PageImpl<>(clientsResponses, pageable, clientsResponses.size());

    //when
    var actual = service.lotteryClients(lotteryId, pageable);

    //then
    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Getting lottery clients when clients don't exist")
  void lotteryClientsWhenNotExist() {
    //given
    var pageable = Pageable.ofSize(10);
    var lotteryId = UUID.randomUUID();
    var clients = new PageImpl<Client>(List.of(), pageable, 0);
    var expected = new PageImpl<ClientShortResponse>(List.of(), pageable, 0);

    when(clientRepository.findAllByLotteriesId(lotteryId, pageable)).thenReturn(clients);

    //when
    var lotteryClients = service.lotteryClients(lotteryId, pageable);

    //then
    assertEquals(expected, lotteryClients);
  }

  @Test
  void clientLotteries() {
  }

  @Test
  void lotteries() {
  }

  @Test
  void overview() {
  }
}