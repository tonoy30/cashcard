package me.tonoy.cashcard;

import me.tonoy.cashcard.dtos.CashCardCreateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cashcards")
public class CashCardController {
    final CashCardRepository cashCardRepository;

    public CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    @GetMapping
    public ResponseEntity<List<CashCard>> findAll(Pageable pageable) {
        Page<CashCard> page = cashCardRepository.findAll(PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Sort.Direction.DESC, "amount")))
        );
        return ResponseEntity.ok(page.getContent());
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<CashCard> findById(@PathVariable String cardId) {
        Optional<CashCard> cashCardOptional = cashCardRepository.findById(cardId);
        return cashCardOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CashCard> createCashCard(@RequestBody CashCardCreateDto cashCardCreateDto, UriComponentsBuilder ucb) {
        CashCard cashCard = new CashCard();
        cashCard.setAmount(cashCardCreateDto.amount());
        CashCard savedCashCard = cashCardRepository.save(cashCard);
        URI locationOfNewCashCard = ucb.path("cashcards/{cardId}").buildAndExpand(savedCashCard.getId()).toUri();
        return ResponseEntity.created(locationOfNewCashCard).build();
    }
}
