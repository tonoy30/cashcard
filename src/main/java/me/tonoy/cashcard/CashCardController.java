package me.tonoy.cashcard;

import me.tonoy.cashcard.dtos.CashCardCreateDto;
import me.tonoy.cashcard.dtos.CashCardUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
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
    public ResponseEntity<List<CashCard>> findAll(Pageable pageable, Principal principal) {
        Page<CashCard> page = cashCardRepository.findByOwner(principal.getName(), PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSortOr(Sort.by(Sort.Direction.DESC, "amount"))));
        return ResponseEntity.ok(page.getContent());
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<CashCard> findById(@PathVariable String cardId, Principal principal) {
        Optional<CashCard> cashCardOptional = Optional.ofNullable(cashCardRepository.findByIdAndOwner(cardId, principal.getName()));
        return cashCardOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CashCard> createCashCard(@RequestBody CashCardCreateDto cashCardCreateDto, UriComponentsBuilder ucb, Principal principal) {
        CashCard cashCard = new CashCard();
        cashCard.setAmount(cashCardCreateDto.amount());
        cashCard.setOwner(principal.getName());
        CashCard savedCashCard = cashCardRepository.save(cashCard);
        URI locationOfNewCashCard = ucb.path("cashcards/{cardId}").buildAndExpand(savedCashCard.getId()).toUri();
        return ResponseEntity.created(locationOfNewCashCard).build();
    }

    @PutMapping("/{cardId}")
    public ResponseEntity<Void> updateCashCard(@PathVariable String cardId, @RequestBody CashCardUpdateDto cashCardUpdateDto, Principal principal) {

        CashCard cashCard = cashCardRepository.findByIdAndOwner(cardId, principal.getName());
        if (cashCard != null) {
            cashCard.setAmount(cashCardUpdateDto.amount());
            cashCardRepository.save(cashCard);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{cardId}")
    private ResponseEntity<Void> deleteCashCard(@PathVariable String cardId, Principal principal) {
        if (cashCardRepository.existsByIdAndOwner(cardId, principal.getName())) {
            cashCardRepository.deleteById(cardId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
