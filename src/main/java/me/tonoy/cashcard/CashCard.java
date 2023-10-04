package me.tonoy.cashcard;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Basic;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.tonoy.cashcard.common.RandomIdGenerator;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
@Slf4j
@Table(name = "cashcards")
public class CashCard {
    @Id
    @GeneratedValue(generator = RandomIdGenerator.ID_GENERATOR_NAME)
    @GenericGenerator(
            name = RandomIdGenerator.ID_GENERATOR_NAME,
            type = RandomIdGenerator.class,
            parameters = {
                    @Parameter(name = RandomIdGenerator.VALUE_PREFIX_PARAMETER, value = "cc"),
                    @Parameter(name = RandomIdGenerator.LENGTH, value = "16")
            })
    @Column(name = "id", updatable = false)
    @Basic(optional = false)
    private String id;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "owner", nullable = false)
    private String owner;
}
