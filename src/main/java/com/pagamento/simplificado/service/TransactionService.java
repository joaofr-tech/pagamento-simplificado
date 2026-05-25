package com.pagamento.simplificado.service;

import com.pagamento.simplificado.domain.Transaction;
import com.pagamento.simplificado.domain.User;
import com.pagamento.simplificado.dtos.TransactionDTO;
import com.pagamento.simplificado.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplate restTemplate;

    public void createTransaction(TransactionDTO transaction) throws Exception {
        User sender = this.userService.findUserById(transaction.senderId());
        User receiver = this.userService.findUserById(transaction.receiverId());

        userService.validateTransaction(sender, transaction.value());

        boolean isAuthorized = this.authorizeTransaction(sender, transaction.value());
        if (!isAuthorized){
            throw new Exception("Transacao nao autorizada");
        }

        Transaction transaction1 = new Transaction();
        transaction1.setAmount(transaction1.getAmount());
        transaction1.setSender(sender);
        transaction1.setReceiver(receiver);
        transaction1.setTimestamp(LocalDateTime.now());

        sender.setBalance(sender.getBalance().subtract(transaction1.getAmount()));
        receiver.setBalance(receiver.getBalance().add(transaction1.getAmount()));

        this.repository.save(transaction1);
        this.userService.saveUser(sender);
        this.userService.saveUser(receiver);
    }

    public boolean authorizeTransaction(User sender, BigDecimal value){
        ResponseEntity<Map> authorizationResponse = restTemplate.getForEntity("https://util.devi.tools/api/v2/authorize", Map.class);

        if (authorizationResponse.getStatusCode() == HttpStatus.OK){
            String message = (String) authorizationResponse.getBody().get("message");
            return "Autorizado".equalsIgnoreCase(message);
        } else return false;
    }
}
