package com.picpaychallenge.services;

import com.picpaychallenge.domain.user.User;
import com.picpaychallenge.domain.user.UserType;
import com.picpaychallenge.dtos.UserDTO;
import com.picpaychallenge.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    public void validateTransaction(User sender, BigDecimal amount) throws Exception {
        if(sender.getUserType() == UserType.MERCHANT){
            throw new Exception("The merchant user is not authorized to complete the transaction.");
        }
        if(sender.getBalance().compareTo(amount) < 0 ){
            throw new Exception("User does not have sufficient balance.");
        }
    }

    public User findUserById(Long id) throws Exception {
        return this.repository.findById(id).orElseThrow(()-> new Exception("User not found"));
    }
    public User createUser(UserDTO data){
        User newUser = new User(data);
        this.saveUser(newUser);
        return newUser;
    }

    public void saveUser(User user){
        this.repository.save(user);
    }
}
