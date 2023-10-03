package me.tonoy.cashcard;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import me.tonoy.cashcard.dtos.CashCardCreateDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CashCardApplicationTests {
    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void shouldReturnCashCardWhenDataIsSaved() {
        String idParam = "cc_LveuRfbywGO9beER";
        ResponseEntity<String> response = restTemplate.getForEntity(String.format("/cashcards/%s", idParam), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        String id = documentContext.read("$.id");
        assertThat(id).isEqualTo(idParam);

        Double amount = documentContext.read("$.amount");
        assertThat(amount).isEqualTo(250.0);
    }

    @Test
    void shouldCreateANewCashCard() {
        CashCardCreateDto cashCardCreateDto = new CashCardCreateDto(250.00);
        ResponseEntity<Void> createResponse = restTemplate.postForEntity("/cashcards", cashCardCreateDto, Void.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI location = createResponse.getHeaders().getLocation();
        ResponseEntity<String> response = restTemplate.getForEntity(location, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
