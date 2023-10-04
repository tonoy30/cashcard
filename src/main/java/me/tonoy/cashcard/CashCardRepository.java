package me.tonoy.cashcard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CashCardRepository extends JpaRepository<CashCard, String>, PagingAndSortingRepository<CashCard, String> {
    CashCard findByIdAndOwner(String id, String owner);

    Page<CashCard> findByOwner(String owner, PageRequest amount);

    boolean existsByIdAndOwner(String id, String owner);
}
