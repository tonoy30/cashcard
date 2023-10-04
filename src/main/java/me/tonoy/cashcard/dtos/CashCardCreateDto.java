package me.tonoy.cashcard.dtos;

import java.util.Optional;

public record CashCardCreateDto(Double amount, Optional<String> owner) {
}

