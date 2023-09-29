package by.vk.tonttery.api.client.model;

import by.vk.tonttery.api.lottery.Lottery;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

@Table(schema = "tonterry", name = "client")
@Getter
@Setter
@ToString
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Client {

  @Id
  private UUID id;
  private Long telegramId;
  private String firstName;
  private String lastName;
  private String telegramUserName;
  private Boolean isBot;
  private Boolean isPremium;
  private String image;
  private LocalDateTime authenticatedAt;
  @CreatedDate
  private LocalDateTime createdAt;
  @LastModifiedDate
  private LocalDateTime updatedAt;
//  @ToString.Exclude
//  private Set<ClientLottery> lotteries;
}

