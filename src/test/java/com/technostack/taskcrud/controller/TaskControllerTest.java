package com.technostack.taskcrud.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TaskControllerTest {

	private static final Pattern ID_PATTERN = Pattern.compile("\"id\"\\s*:\\s*(\\d+)");

	@Autowired
	private MockMvc mockMvc;

	@Test
	void createsListsUpdatesAndDeletesATask() throws Exception {
		String created = mockMvc.perform(post("/api/tasks")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"title":"Write Jenkinsfile","description":"First CI pipeline","status":"TODO"}
								"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.title").value("Write Jenkinsfile"))
				.andExpect(jsonPath("$.status").value("TODO"))
				.andReturn()
				.getResponse()
				.getContentAsString();

		Matcher matcher = ID_PATTERN.matcher(created);
		if (!matcher.find()) {
			throw new AssertionError("Created task response did not include an id: " + created);
		}
		long id = Long.parseLong(matcher.group(1));

		mockMvc.perform(get("/api/tasks"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)));

		mockMvc.perform(get("/api/tasks/{id}", id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("Write Jenkinsfile"));

		mockMvc.perform(put("/api/tasks/{id}", id)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"title":"Write Jenkinsfile","description":"Pipeline is green","status":"DONE"}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("DONE"))
				.andExpect(jsonPath("$.description").value("Pipeline is green"));

		mockMvc.perform(delete("/api/tasks/{id}", id))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/tasks/{id}", id))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Task not found: " + id));
	}

	@Test
	void rejectsABlankTitle() throws Exception {
		mockMvc.perform(post("/api/tasks")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"title":"  "}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Bad Request"));
	}

	@Test
	void defaultsStatusToTodoWhenOmitted() throws Exception {
		mockMvc.perform(post("/api/tasks")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"title\":\"Learn Jenkins\"}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.status").value("TODO"));
	}
}
