package com.beginsecure.usersbchallenge;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetUsers() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/api/users/getUsers")
            .content(
                """
                {
                    "Token": "",
                    "Content": {
                            "id": 1
                        }
                    }
                }
                """
                ))
            .andExpect(status().isUnauthorized());
    }
    
	@Test
    public void testCreateUser_valid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/api/users/createUser")
            .content(
                String.format(
				"""
				{
					"Content": {
						"name": "Zé TEST",
						"email": "%s@outlook.com",
						"password": "123456789",
						"birthdate": "1992-12-21"
					}
				}
                """, UUID.randomUUID().toString())
			))
			.andExpect(status().isOk());
    }

	@Test
    public void testCreateUser_invalidPw() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/api/users/createUser")
            .content(
                String.format(
				"""
				{
					"Content": {
						"name": "Zé TEST",
						"email": "%s@outlook.com",
						"password": "1234567",
						"birthdate": "1992-12-21"
					}
				}
                """, UUID.randomUUID().toString())
			))
			.andExpect(status().isInternalServerError());
    }
}