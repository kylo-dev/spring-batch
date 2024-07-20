package comento.chat.batch.config;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableConfigurationProperties
public class DataSourceProperties {

    @Bean(name = "chatDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.chat-db")
    public DataSource chatDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Primary
    @Bean(name = "coreDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.core-db")
    public DataSource coreDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }
}
