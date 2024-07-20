package comento.chat.batch.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "coreEntityManagerFactory",
        basePackages = {"comento.chat.batch.chat.corerepository",
                "comento.chat.batch.chatlike.corerepository"}
)
public class CoreEntityManagerFactoryConfig {

    @Autowired
    @Qualifier("coreDataSource")
    private DataSource coreDataSource;

    @Primary
    @Bean(name = "coreEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean coreEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(coreDataSource)
                .packages("comento.chat.batch.chat", "comento.chat.batch.chatlike")
                .persistenceUnit("CoreUnit")
                .build();
    }
}
