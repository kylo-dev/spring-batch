package comento.chat.batch.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "chatEntityManagerFactory",
        basePackages = {"comento.chat.batch.chat.repository",
                "comento.chat.batch.chatlike.repository"}
)
public class ChatEntityManagerFactoryConfig {

    @Autowired
    @Qualifier("chatDataSource")
    private DataSource chatDataSource;

    @Bean(name = "chatEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean chatEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder.dataSource(chatDataSource)
                .packages("comento.chat.batch.chat", "comento.chat.batch.chatlike")
                .persistenceUnit("ChatUnit")
                .build();
    }
}
