package com.mkyong.web;

import com.mkyong.web.config.SpringConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = SpringConfig.class)
public class TestWelcome {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webAppContext;
    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }
    @Test
    public void testWelcomeMsg() throws Exception {

        this.mockMvc.perform(
                get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(forwardedUrl("/WEB-INF/views/index.jsp"))
                .andExpect(model().attribute("msg", "Hello World - Prasanna welcomes you."));

    }
    @Test
        public void testEnvMsg() throws Exception {
	
	this.mockMvc.perform(	
		get("/"))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(view().name("index"))
		.andExpect(forwardedUrl("/WEB-INF/views/index.jsp"))
		.andExpect(model().attribute("env", "dev"));

    }
    @Test
    public void testRandomNumber() {
        assertEquals(12, 6 + 6);
    }
    @Test
    public void testRandomString() {
        assertEquals("India", "India");
  }
}

