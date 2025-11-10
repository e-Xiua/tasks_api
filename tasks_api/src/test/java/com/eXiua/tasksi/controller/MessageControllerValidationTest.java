package com.eXiua.tasksi.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.eXiua.tasksi.service.MessageService;

@ExtendWith(MockitoExtension.class)
public class MessageControllerValidationTest {

    private MockMvc mockMvc;

    private MessageService messageService;

    @BeforeEach
    public void setup() {
        messageService = mock(MessageService.class);
        MessageController controller = new MessageController();
        ReflectionTestUtils.setField(controller, "messageService", messageService);
        LocalValidatorFactoryBean val = new LocalValidatorFactoryBean();
        val.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(val)
                .build();
    }

    @Test
    public void postInvalidMessage_returns400() throws Exception {
        String body = "{\"senderId\":\"\",\"receiverId\":\"r1\",\"content\":\"\"}";

        mockMvc.perform(post("/api/messages/task/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").isArray());
    }
}
