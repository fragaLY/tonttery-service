package by.vk.tonttery.api.client.repository;

import by.vk.tonttery.api.client.model.Client;
import java.util.UUID;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface Repository extends R2dbcRepository<Client, UUID> {

  String CLIENT_PAGEABLE_LOTTERIES_BY_STATUS_QUERY_VALUE = """
      SELECT c.id as client_id, c.telegram_id as telegram_id, c.first_name as first_name, c.last_name as last_name,
      c.telegram_username as telegram_username, c.is_bot as is_bot, c.is_premium as is_premium, c.authenticated_at as authenticated_at,
      c.created_at as created_at, c.updated_at as updated_at, l.id as lottery_id, l.type as type, l.status as status,
      l.start_date as start_date, l.created_at as created_at,
      l.updated_at as updated_at FROM client_lottery cl JOIN client c on c.id = cl.client_id WHERE l.status in (:statuses) AND c.id = :clientId;
      """;

}
