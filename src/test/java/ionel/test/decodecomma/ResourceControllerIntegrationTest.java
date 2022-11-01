package ionel.test.decodecomma;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.net.URI;
import java.util.List;

@SpringBootTest
class ResourceControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void baseSetup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webContext).build();
    }

	@Test
	void singleValue() throws Exception {
		// using URI so that no further encoding takes place
		URI uri = new URI("/test?res=xx%2Cyy");

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(uri))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();

		byte[] content = mvcResult.getResponse().getContentAsByteArray();
		List<String> returnedResources = objectMapper.readValue(content, new TypeReference<>() {
		});
		Assertions.assertThat(returnedResources).containsExactly("xx,yy");
	}

    @ParameterizedTest
	@ValueSource(strings = {"/test?res=xx,yy%2Czz", "/test?res=xx&res=yy%2Czz"})
    void twoValues(String queryString) throws Exception {
		// using URI so that no further encoding takes place
		URI uri = new URI(queryString);

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(uri))
				.andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        byte[] content = mvcResult.getResponse().getContentAsByteArray();
        List<String> returnedResources = objectMapper.readValue(content, new TypeReference<>() {
        });
        Assertions.assertThat(returnedResources).containsExactlyInAnyOrder("xx", "yy,zz");
    }

}
