package com.service.emailsender.config;

import com.service.emailsender.service.RedisMessageSubcriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
@EnableRedisRepositories
public class RedisListenerConfig {

    @Value("${spring.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.redis.port:6379}")
    private Integer redisPort;

    @Value("${spring.redis.channel-name:emailSender}")
    private String redisChannelName;

    @Autowired
    private RedisMessageSubcriber redisMessageSubcriber;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(){
        RedisStandaloneConfiguration redisCon = new RedisStandaloneConfiguration(redisHost, redisPort);
        return new LettuceConnectionFactory(redisCon);
    }

    @Bean
    public MessageListener messageListenerAdapter(){
        return new MessageListenerAdapter(redisMessageSubcriber);
    }

    @Bean
    public ChannelTopic channelTopic(){
        return new ChannelTopic(redisChannelName);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(){
        RedisMessageListenerContainer container =  new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory());
        container.addMessageListener(messageListenerAdapter(), channelTopic());
        return container;
    }
}
