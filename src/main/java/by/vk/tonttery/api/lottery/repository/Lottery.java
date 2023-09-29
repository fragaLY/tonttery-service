package by.vk.tonttery.api.lottery.repository;

import by.vk.tonttery.api.client.repository.Client;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;

/**
 * The entity of lottery.
 */
@Table(schema = "tonttery", name = "lottery")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"id", "winner", "clients"})
public class Lottery {

  public Lottery(Type type, LocalDate startDate) {
    this.status = Status.CREATED;
    this.type = type;
    this.startDate = startDate;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @OneToOne
  @ToString.Exclude
  private Client winner;

  private Type type;

  private Status status;

  private LocalDate startDate;

  @JoinTable(name = "client_lottery", joinColumns = @JoinColumn(name = "lottery_id"), inverseJoinColumns = @JoinColumn(name = "client_id"))
  @ManyToMany
  @OrderBy("telegramUserName")
  @ToString.Exclude
  private Set<Client> clients;

  @Column(name = "created_at", updatable = false)
  @CreationTimestamp(source = SourceType.DB)
  private LocalDateTime createdAt;

  @CreationTimestamp(source = SourceType.DB)
  private LocalDateTime updatedAt;
}

