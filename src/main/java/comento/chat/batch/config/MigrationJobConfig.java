package comento.chat.batch.config;

import comento.chat.batch.chat.Chat;
import comento.chat.batch.chatlike.ChatLike;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;

@Configuration
public class MigrationJobConfig {

    @Autowired
    @Qualifier("coreEntityManagerFactory")
    private EntityManagerFactory coreEntityManagerFactory;

    @Autowired
    @Qualifier("chatEntityManagerFactory")
    private EntityManagerFactory chatEntityManagerFactory;

    @Bean
    public Job chatJob(JobRepository jobRepository) {
        return new JobBuilder("chatJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(chatStep(jobRepository))
                .next(chatLikeStep(jobRepository))
                .build();
    }

    @Bean
    public Step chatStep(JobRepository jobRepository) {

        return new StepBuilder("chatStep", jobRepository)
                .<Chat, Chat>chunk(100, new JpaTransactionManager(chatEntityManagerFactory))
                .reader(chatReader())
                .writer(chatWriter())
                .build();
    }

    @Bean
    public Step chatLikeStep(JobRepository jobRepository) {

        return new StepBuilder("chatLikeStep", jobRepository)
                .<ChatLike, ChatLike>chunk(100, new JpaTransactionManager(chatEntityManagerFactory))
                .reader(chatLikeReader())
                .writer(chatLikeWriter())
                .build();
    }

//    @Bean
//    public Job chatLikeJob(JobRepository jobRepository, Step chatLikeStep) {
//        return new JobBuilder("chatLikeJob", jobRepository)
//                .start(chatLikeStep)
//                .build();
//    }

//    @Bean
//    public Step chatStep(JobRepository jobRepository, ItemReader<Chat> chatReader,
//            ItemWriter<Chat> chatWriter) {
//
//        return new StepBuilder("chatStep", jobRepository)
//                .<Chat, Chat>chunk(100, new JpaTransactionManager(chatEntityManagerFactory))
//                .reader(chatReader)
//                .writer(chatWriter)
//                .build();
//    }
//
//    @Bean
//    public Step chatLikeStep(JobRepository jobRepository, ItemReader<ChatLike> chatLikeReader,
//            ItemWriter<ChatLike> chatLikeWriter) {
//
//        return new StepBuilder("chatLikeStep", jobRepository)
//                .<ChatLike, ChatLike>chunk(100, new JpaTransactionManager(chatEntityManagerFactory))
//                .reader(chatLikeReader)
//                .writer(chatLikeWriter)
//                .build();
//    }

    @Bean
    public ItemReader<Chat> chatReader() {
        // 데이터 조회
        return new JpaPagingItemReaderBuilder<Chat>()
                .name("chatReader")
                .entityManagerFactory(coreEntityManagerFactory)
                .pageSize(100)
                .queryString("select c from Chat c")
                .build();
    }

    @Bean
    public ItemReader<ChatLike> chatLikeReader() {
        return new JpaPagingItemReaderBuilder<ChatLike>()
                .name("chatLikeReader")
                .entityManagerFactory(coreEntityManagerFactory)
                .pageSize(100)
                .queryString("select cl from ChatLike cl")
                .build();
    }

    @Bean
    public JpaItemWriter<Chat> chatWriter() {
        JpaItemWriter<Chat> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(chatEntityManagerFactory);
        writer.setUsePersist(true);
        return writer;
    }

    @Bean
    JpaItemWriter<ChatLike> chatLikeWriter() {
        JpaItemWriter<ChatLike> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(chatEntityManagerFactory);
        writer.setUsePersist(true);
        return writer;
    }

}
