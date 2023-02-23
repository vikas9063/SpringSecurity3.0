package com.vk.proj.service;

import com.vk.proj.modal.Users;
import com.vk.proj.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    public String userIdByUserName(String userName) throws Exception {
        Users users= this.userRepo.findByEmail(userName);
        if(users == null){
            throw new Exception("User Not Found !!!");
        }
        return users.getId();
    }
    public double getUserDailyLimit(String userName) throws  Exception{
        Users users= this.userRepo.findByEmail(userName);
        if(users == null){
            throw new Exception("User Not Found !!!");
        }
        return users.getDailyLimit();
    }

}
