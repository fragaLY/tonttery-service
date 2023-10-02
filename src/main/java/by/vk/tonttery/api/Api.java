/**
 * Copyright © 2023-2024 Vadzim Kavalkou. All Rights Reserved. All information contained herein is,
 * and remains the property of Vadzim Kavalkou and/or its suppliers and is protected by
 * international intellectual property law. Dissemination of this information or reproduction of
 * this material is strictly forbidden, unless prior written permission is obtained from Vadzim
 * Kavalkou.
 **/

package by.vk.tonttery.api;

import by.vk.tonttery.api.client.response.ClientResponse;
import by.vk.tonttery.api.client.response.ClientShortResponse;
import by.vk.tonttery.api.lottery.response.LotteryResponse;
import by.vk.tonttery.api.lottery.response.LotteryShortResponse;
import by.vk.tonttery.api.service.TonterryService;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * The API of application. Self-documented by openapi.
 * See swagger documentation in README.
 *
 * @param service - the tonttery service with all business logic.
 */
@RestController
@RequestMapping(value = "api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public record Api(TonterryService service) {

  @GetMapping("/clients/{clientId}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<ClientResponse> one(
      @NotNull(message = "The id of client should not be null") @PathVariable(name = "clientId") UUID clientId) {
    return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
        .body(service.client(clientId));
  }

  @GetMapping("/clients/{clientId}/lotteries")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Page<LotteryShortResponse>> clientLotteries(
      @NotNull(message = "The client's should not be null") @PathVariable(name = "clientId") UUID clientId,
      @SortDefault(sort = "startDate", direction = Sort.Direction.DESC) Pageable pageable) {
    return ResponseEntity.ok().cacheControl(CacheControl.noCache())
        .body(service.clientLotteries(clientId, pageable));
  }

  @GetMapping("/lotteries")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Page<LotteryShortResponse>> lotteries(
      @SortDefault.SortDefaults({@SortDefault(sort = "startDate", direction = Sort.Direction.DESC),
          @SortDefault(sort = "type", direction = Sort.Direction.DESC),
          @SortDefault(sort = "status", direction = Sort.Direction.DESC)}) Pageable pageable) {
    return ResponseEntity.ok().cacheControl(CacheControl.noCache())
        .body(service.lotteries(pageable));
  }

  @GetMapping("/lotteries/{lotteryId}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<LotteryResponse> lottery(
      @NotNull(message = "The id of lottery should not be null") @PathVariable(name = "lotteryId") UUID lotteryId,
      @RequestParam(name = "clientId", required = false) UUID clientId) {
    return ResponseEntity.ok().cacheControl(CacheControl.noCache())
        .body(service.lottery(lotteryId, clientId));
  }

  @GetMapping("/lotteries/{lotteryId}/clients")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Page<ClientShortResponse>> lotteryClients(
      @NotNull(message = "The id of lottery should not be null") @PathVariable(name = "lotteryId") UUID lotteryId,
      @SortDefault.SortDefaults({@SortDefault(sort = "isPremium", direction = Sort.Direction.DESC),
          @SortDefault(sort = "telegramUserName", direction = Sort.Direction.DESC)}) Pageable pageable) {
    return ResponseEntity.ok().cacheControl(CacheControl.noCache())
        .body(service.lotteryClients(lotteryId, pageable));
  }

  @PostMapping("/lotteries/{lotteryId}/clients/{clientId}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<LotteryResponse> join(
      @NotNull(message = "The id of lottery should not be null") @PathVariable(name = "lotteryId") UUID lotteryId,
      @NotNull(message = "The client's should not be null") @PathVariable(name = "clientId") UUID clientId) {
    return ResponseEntity.ok().cacheControl(CacheControl.noCache())
        .body(service.join(lotteryId, clientId));
  }

  @DeleteMapping("/lotteries/{lotteryId}/clients/{clientId}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<LotteryResponse> cancel(
      @NotNull(message = "The id of lottery should not be null") @PathVariable(name = "lotteryId") UUID lotteryId,
      @NotNull(message = "The client's should not be null") @PathVariable(name = "clientId") UUID clientId) {
    return ResponseEntity.ok().cacheControl(CacheControl.noCache())
        .body(service.cancel(lotteryId, clientId));
  }

}
