package comento.chat.batch.chatlike.corerepository;

import comento.chat.batch.chatlike.ChatLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatLikeCoreRepository extends JpaRepository<ChatLike, Long> {

}
