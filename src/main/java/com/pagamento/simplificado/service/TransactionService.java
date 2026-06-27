package com.pagamento.simplificado.service;

import com.pagamento.simplificado.domain.Transaction;
import com.pagamento.simplificado.domain.User;
import com.pagamento.simplificado.dtos.TransactionDTO;
import com.pagamento.simplificado.repositories.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TransactionService {

    private final TransactionRepository repository;

    private final UserService userService;

    private final RestTemplate restTemplate;
    
    private final NotificationService notificationService;

    TransactionService(TransactionRepository repository, UserService userService, RestTemplate restTemplate, NotificationService notificationService) {
        this.repository = repository;
        this.userService = userService;
        this.restTemplate = restTemplate;
        this.notificationService = notificationService;
    }

    @Transactional
    public Transaction createTransaction(TransactionDTO transaction) throws Exception {
        User sender = this.userService.findUserById(transaction.senderId());
        User receiver = this.userService.findUserById(transaction.receiverId());

        userService.validateTransaction(sender, transaction.value());

        boolean isAuthorized = this.authorizeTransaction(sender, transaction.value());
        if (!isAuthorized){
            throw new Exception("Transacao nao autorizada");
        }

        Transaction transaction1 = new Transaction();
        transaction1.setAmount(transaction.value());
        transaction1.setSender(sender);
        transaction1.setReceiver(receiver);
        transaction1.setTimestamp(LocalDateTime.now());

        sender.setBalance(sender.getBalance().subtract(transaction1.getAmount()));
        receiver.setBalance(receiver.getBalance().add(transaction1.getAmount()));

        this.repository.save(transaction1);
        this.userService.saveUser(sender);
        this.userService.saveUser(receiver);
        this.notificationService.sendNotification(sender, "transacao concluida");
        this.notificationService.sendNotification(receiver, "transacao concluida");

        return transaction1;
    }

    public boolean authorizeTransaction(User sender, BigDecimal value){
        try {
            ResponseEntity<Map> authorizationResponse = restTemplate.getForEntity("https://util.devi.tools/api/v2/authorize", Map.class);

            if (authorizationResponse.getStatusCode() != HttpStatus.OK || authorizationResponse.getBody() == null){
                System.out.println("Servico de autorizacao indisponivel (status: " + authorizationResponse.getStatusCode() + "). Prosseguindo com a transacao.");
                return true;
            }

            Map body = authorizationResponse.getBody();
            Object message = body.get("message");
            if (message instanceof String && "Autorizado".equalsIgnoreCase((String) message)){
                return true;
            }

            Object data = body.get("data");
            if (data instanceof Map){
                Object authorization = ((Map) data).get("authorization");
                if (Boolean.TRUE.equals(authorization)) {
                    return true;
                }
            }

            return false;
        } catch (RestClientException exception) {
            System.out.println("Servico de autorizacao indisponivel: " + exception.getMessage() + ". Prosseguindo com a transacao.");
            return true;
        }
    }
}
