package comento.chat.batch.chat.repository;

import comento.chat.batch.chat.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {

}
