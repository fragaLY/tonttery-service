package by.vk.tonttery.api.client.repository;

import by.vk.tonttery.api.lottery.repository.Lottery;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * The client's entity.
 */
@Table(schema = "tonttery", name = "client")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"id", "lotteries"})
public class Client {

  @EmbeddedId
  private ClientId id;

  private String firstName;

  private String lastName;

  private String telegramUserName;

  private Boolean isBot;

  private Boolean isPremium;

  private String image;

  private LocalDateTime authenticatedAt;

  @ManyToMany(mappedBy = "clients")
  @OrderBy("startDate")
  @ToString.Exclude
  private Set<Lottery> lotteries;

  @Column(name = "created_at", updatable = false)
  @CreationTimestamp(source = SourceType.DB)
  private LocalDateTime createdAt;

  @UpdateTimestamp(source = SourceType.DB)
  private LocalDateTime updatedAt;
}

