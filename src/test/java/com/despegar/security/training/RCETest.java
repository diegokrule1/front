package com.mydomain.security.training;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.mydomain.security.training.front.Front;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Front.class)
@AutoConfigureMockMvc
public class RCETest {

	@Autowired
	protected MockMvc mvc;
	
	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
	
	@Test
	public void testRCE() throws Exception{
		
		 this.mvc
	                .perform(get("/rce").param("data", "rO0ABXNyADJjb20uZGVzcGVnYXIuc2VjdXJpdHkudHJhaW5pbmcuZnJvbnQuZ2FkZ2V0LkdhZGdldPutZp6pR2MoAgABTAAFcGFyYW10ABJMamF2YS9sYW5nL1N0cmluZzt4cHQAEm9wZW4gLWEgY2FsY3VsYXRvcg==").accept(APPLICATION_JSON_UTF8)
	                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
	                .andExpect(status().is5xxServerError());
		
	}

}
