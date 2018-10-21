package ru.XXXXXXXXX.renovation.config;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import ru.XXXXXXXXX.common.util.*;
import ru.XXXXXXXXX.data.utils.IntegerDeserializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;

@Configuration
@Slf4j
public class Jackson2ObjectMapperBuilderConfigurationForRenovation {

    @Bean
    public ObjectMapper objectMapper() {
       
        Hibernate5Module hm = new Hibernate5Module();
        hm.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true);

        // кастомная сериализация для Java 8 Time
        SimpleModule sm = new SimpleModule("JSR-310 XXXXXXXXX Custom deserialization", new Version(1,0,0,null, null, null));
        sm.addSerializer(new LocalDateSerializer());
        sm.addSerializer(new LocalDateTimeSerializer());
        sm.addSerializer(new LocalTimeSerializer());
        sm.addSerializer(new ZonedDateTimeSerializer());

        sm.addDeserializer(LocalDate.class, new LocalDateDeserializer());
        sm.addDeserializer(LocalTime.class, new LocalTimeDeserializer());
        sm.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        sm.addDeserializer(ZonedDateTime.class, new ZonedDateTimeDeserializer());
        sm.addDeserializer(Integer.class, new IntegerDeserializer());

        return new Jackson2ObjectMapperBuilder()
                .modulesToInstall(hm, sm)
                .build();
    }

}
