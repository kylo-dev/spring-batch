package comento.chat.batch.chat.corerepository;

import comento.chat.batch.chat.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatCoreRepository extends JpaRepository<Chat, Long> {

}
