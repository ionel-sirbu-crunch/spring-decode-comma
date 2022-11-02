package ionel.test.decodecomma;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

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

    @Test
    void space() throws Exception {
        String resourceValue = URLEncoder.encode("xx yy", StandardCharsets.UTF_8);
        // using URI so that no further encoding takes place
        URI uri = new URI("/test?res=" + resourceValue);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(uri))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        byte[] content = mvcResult.getResponse().getContentAsByteArray();
        List<String> returnedResources = objectMapper.readValue(content, new TypeReference<>() {
        });
        Assertions.assertThat(returnedResources).containsExactly("xx yy");
    }

    @ParameterizedTest
    @MethodSource("getSpecialChars")
    void specialChars(char c) throws Exception {
        String resourceValue = "xx" + c + "yy";
        String encodedResourceValue = URLEncoder.encode(resourceValue, StandardCharsets.UTF_8);
        // using URI so that no further encoding takes place
        URI uri = new URI("/test?res=" + encodedResourceValue);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(uri))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        byte[] content = mvcResult.getResponse().getContentAsByteArray();
        List<String> returnedResources = objectMapper.readValue(content, new TypeReference<>() {
        });
        Assertions.assertThat(returnedResources).containsExactly(resourceValue);
    }

    public static Stream<Arguments> getSpecialChars() {
        // only `,` & space fail from this set
        return "`¬!\"£$%^&*()-_=+[]{};:'@#~\\|,.<>/? \t\r\n".chars()
                .mapToObj(i -> Arguments.of((char) i));
    }

}
