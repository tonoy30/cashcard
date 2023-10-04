package me.tonoy.cashcard;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import me.tonoy.cashcard.common.RandomString;
import me.tonoy.cashcard.dtos.CashCardCreateDto;
import me.tonoy.cashcard.dtos.CashCardDeleteDto;
import me.tonoy.cashcard.dtos.CashCardUpdateDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CashCardApplicationTests {
    @Autowired
    TestRestTemplate restTemplate;
    private static final String ID_PARAM = "cc_LveuRfbywGO9beER";
    private static final String USER_NAME = "tonoy30";
    private static final String PASSWORD = "Password1244";

    @Test
    void shouldReturnCashCardWhenDataIsSaved() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth(USER_NAME, PASSWORD)
                .getForEntity(String.format("/cashcards/%s", "cc_N1UXv7E9q8tAwx1D"), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        String id = documentContext.read("$.id");
        assertThat(id).isEqualTo("cc_N1UXv7E9q8tAwx1D");

        Double amount = documentContext.read("$.amount");
        assertThat(amount).isEqualTo(250.0);
    }

    @Test
    @DirtiesContext
    void shouldCreateANewCashCard() {
        CashCardCreateDto cashCardCreateDto = new CashCardCreateDto(250.00, Optional.empty());
        ResponseEntity<Void> createResponse = restTemplate
                .withBasicAuth(USER_NAME, PASSWORD)
                .postForEntity("/cashcards", cashCardCreateDto, Void.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI location = createResponse.getHeaders().getLocation();
        ResponseEntity<String> response = restTemplate
                .withBasicAuth(USER_NAME, PASSWORD)
                .getForEntity(location, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldNotReturnACashCardWhenUsingBadCredentials() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth(USER_NAME, RandomString.next("pass", Optional.empty()))
                .getForEntity(String.format("/cashcards/%s", ID_PARAM), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        response = restTemplate
                .withBasicAuth(RandomString.next("user", Optional.empty()), PASSWORD)
                .getForEntity(String.format("/cashcards/%s", ID_PARAM), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldRejectUsersWhoAreNotCardOwners() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("joy97", PASSWORD)
                .getForEntity(String.format("/cashcards/%s", "cc_XpAibDrp9RdhIcw5"), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldNotAllowAccessToCashCardsTheyDoNotOwn() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth(USER_NAME, PASSWORD)
                .getForEntity(String.format("/cashcards/%s", "cc_XpAibDrp9RdhIcw5"), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void shouldUpdateAnExistingCashCard() {
        CashCardUpdateDto cashCardUpdate = new CashCardUpdateDto("cc_aIdEBYXYh4kXY1ts", 300.90);
        HttpEntity<CashCardUpdateDto> request = new HttpEntity<>(cashCardUpdate);
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth(USER_NAME, PASSWORD)
                .exchange(String.format("/cashcards/%s", cashCardUpdate.id()), HttpMethod.PUT, request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth(USER_NAME, PASSWORD)
                .getForEntity(String.format("/cashcards/%s", cashCardUpdate.id()), String.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());

        Double amount = documentContext.read("$.amount");
        assertThat(amount).isEqualTo(cashCardUpdate.amount());
    }

    @Test
    void shouldNotUpdateACashCardThatDoesNotExist() {
        CashCardUpdateDto cashCardUpdate = new CashCardUpdateDto("cc_aIdEBYXYh4kXY1tt", 300.90);
        HttpEntity<CashCardUpdateDto> request = new HttpEntity<>(cashCardUpdate);
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth(USER_NAME, PASSWORD)
                .exchange(String.format("/cashcards/%s", cashCardUpdate.id()), HttpMethod.PUT, request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void shouldDeleteAnExistingCashCard() {
        CashCardDeleteDto cashCardDeleteDto = new CashCardDeleteDto("cc_oojzHSTQB8W93MHD");
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth(USER_NAME, PASSWORD)
                .exchange(String.format("/cashcards/%s", cashCardDeleteDto.id()), HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth(USER_NAME, PASSWORD)
                .getForEntity(String.format("/cashcards/%s", cashCardDeleteDto.id()), String.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotDeleteACashCardThatDoesNotExist() {
        CashCardDeleteDto cashCardDeleteDto = new CashCardDeleteDto("cc_CsS7Tx7ay8GmAArZ");
        ResponseEntity<Void> deleteResponse = restTemplate
                .withBasicAuth(USER_NAME, PASSWORD)
                .exchange(String.format("/cashcards/%s", cashCardDeleteDto.id()), HttpMethod.DELETE, null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
    @Test
    void shouldNotAllowDeletionOfCashCardsTheyDoNotOwn() {
        CashCardDeleteDto cashCardDeleteDto = new CashCardDeleteDto("cc_8nPXKF3oFNlNM0by");
        ResponseEntity<Void> deleteResponse = restTemplate
                .withBasicAuth(USER_NAME, PASSWORD)
                .exchange(String.format("/cashcards/%s", cashCardDeleteDto.id()), HttpMethod.DELETE, null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
