package com.rabbitmq.producer;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RabbitMQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    //回调函数，confirm确认
    final ConfirmCallback confirmCallback = new ConfirmCallback() {
        @Override
        public void confirm(@Nullable CorrelationData correlationData, boolean ack, @Nullable String cause) {
            System.out.println("correlationData: " + correlationData);
            System.out.println("ack: " + ack);
            if (!ack) {
                System.out.println("异常处理......");
            }
        }
    };

    //回调函数，return返回
    final ReturnCallback returnCallback = new ReturnCallback() {
        @Override
        public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
            System.out.println("exchange: " + exchange + ",routingKey: " + routingKey +
                                ",replyCode: " + replyCode + ",replyText: " + replyText);
        }
    };

    //发送消息方法调用：构建Message消息
    public void send(Object message, Map<String, Object> properties) throws Exception {
        MessageHeaders mhs = new MessageHeaders(properties);
        org.springframework.messaging.Message msg = MessageBuilder.createMessage(message, mhs);
        rabbitTemplate.setConfirmCallback(confirmCallback);
        rabbitTemplate.setReturnCallback(returnCallback);
        CorrelationData correlationData = new CorrelationData("1234567890");
        rabbitTemplate.convertAndSend("exchange-1", "springboot.abc", msg, correlationData);
    }

}
