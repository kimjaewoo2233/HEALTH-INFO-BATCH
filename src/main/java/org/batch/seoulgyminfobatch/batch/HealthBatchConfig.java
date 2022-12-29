package org.batch.seoulgyminfobatch.batch;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.batch.seoulgyminfobatch.domain.HealthInfo;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class HealthBatchConfig {

        private final JobBuilderFactory jobBuilderFactory;
        private final StepBuilderFactory stepBuilderFactory;

        private final EntityManagerFactory entityManagerFactory;

        private final DataSource dataSource;



        @Bean
        public Job job() throws Exception {
            return jobBuilderFactory
                    .get("healthInfoJob")
                    .incrementer(new RunIdIncrementer())
                    .start(this.step())
                    .build();
        }

        @Bean
        @JobScope
        public Step step() throws Exception {

            return stepBuilderFactory
                    .get("healthInfoStep")
                    .<HealthInfo,HealthInfo>chunk(10)
                    .reader(this.healthStepReader())    //FlatFileItemReader
                    .writer(this.healthStepWriterJDBC())    //JdbcBathItemWriter
                    .build();

        }
        private ItemReader<? extends HealthInfo> healthStepReader() throws Exception {
            //DefaultLineMapper, DelimitedLineTokenizer, FlatFileItemReader
            DefaultLineMapper<HealthInfo> lineMapper = new DefaultLineMapper<>();
            DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();

            tokenizer.setNames("category","brandName","landNumber","roadNumber");
            lineMapper.setLineTokenizer(tokenizer);

            lineMapper.setFieldSetMapper(fieldSet -> {
                return HealthInfo.builder()
                        .category(fieldSet.readString("category"))
                        .brandName(fieldSet.readString("brandName"))
                        .landNumber(fieldSet.readString("landNumber"))
                        .roadNumber(fieldSet.readString("roadNumber"))
                        .build();
            });

            FlatFileItemReader<HealthInfo> itemReader =
                    new FlatFileItemReaderBuilder<HealthInfo>()
                            .name("healthCSVItemReader")
                            .encoding("x-windows-949")
                            .resource(new ClassPathResource("health-info.csv"))
                            .lineMapper(lineMapper)
                            .linesToSkip(1)
                            .build();

            itemReader.afterPropertiesSet();
            return itemReader;
        }
        private ItemWriter<? super HealthInfo> healthStepWriter() throws Exception {
            JpaItemWriter<HealthInfo> healthInfoJpaItemWriter = new JpaItemWriterBuilder<HealthInfo>()
                    .entityManagerFactory(entityManagerFactory)
                    .usePersist(true)
                    .build();

            healthInfoJpaItemWriter.afterPropertiesSet();

            return healthInfoJpaItemWriter;


        }

        //JDBC를 활용한 jdbc 한 줄 쿼리
        public ItemWriter<HealthInfo> healthStepWriterJDBC() throws Exception {
            JdbcBatchItemWriter<HealthInfo> jdbcBatchItemWriter = new JdbcBatchItemWriterBuilder<HealthInfo>()
                    .dataSource(dataSource)
                    .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                    .sql("insert into health_info(brand_name, land_number, road_number, category) " +
                            "values(:brandName, :landNumber, :roadNumber, :category)")
                    .build();

            jdbcBatchItemWriter.afterPropertiesSet();

            return jdbcBatchItemWriter;
        }


    }
