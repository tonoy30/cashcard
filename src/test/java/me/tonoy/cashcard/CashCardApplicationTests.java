package me.tonoy.cashcard;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CashCardApplicationTests {

    @Test
    void contextLoads() {
        assertThat(42).isEqualTo(42);
    }
}
