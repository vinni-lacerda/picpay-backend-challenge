package com.picpaychallenge.services;

import com.picpaychallenge.domain.transaction.Transaction;
import com.picpaychallenge.domain.user.User;
import com.picpaychallenge.dtos.TransactionDTO;
import com.picpaychallenge.repositories.TransactionRepository;
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
    private TransactionRepository transactionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplate restTemplate;

    public void createTransaction(TransactionDTO transaction) throws Exception {
        User sender = this.userService.findUserById(transaction.senderId());
        User receiver = this.userService.findUserById(transaction.receiverID());
        userService.validateTransaction(sender, transaction.value());

        boolean isAuthorized = this.authorizedTransaction(sender, transaction.value());
        if(!isAuthorized){
            throw new Exception("Transaction unauthorized");
        }

        Transaction newTransaction = new Transaction();
        newTransaction.setAmount(transaction.value());
        newTransaction.setSender(sender);
        newTransaction.setReciever(receiver);
        newTransaction.setTimestamp(LocalDateTime.now());

        sender.setBalance(sender.getBalance().subtract(transaction.value()));
        receiver.setBalance(receiver.getBalance().add(transaction.value()));

        this.transactionRepository.save(newTransaction);
        this.userService.saveUser(sender);
        this.userService.saveUser(receiver);

    }

    public boolean authorizedTransaction(User sender, BigDecimal value){
       ResponseEntity<Map> authorizationResponse = restTemplate.getForEntity("https://util.devi.tools/api/v2/authorize", Map.class);
       if(authorizationResponse.getStatusCode() == HttpStatus.OK && authorizationResponse.getBody().get("message")== "Autorizado"){
           return true;
       }else return false;
    }
}
