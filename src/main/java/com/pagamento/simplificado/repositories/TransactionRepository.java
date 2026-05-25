package com.pagamento.simplificado.repositories;

import com.pagamento.simplificado.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
