package com.vk.proj.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.vk.proj.modal.Users;

public interface UserRepo extends MongoRepository<Users, String>{

    Users findByEmail(String email);
}
