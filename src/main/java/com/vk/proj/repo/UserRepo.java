package com.vk.proj.repo;

import org.springframework.data.jpa.repository.JpaRepository;


import com.vk.proj.modal.Users;

public interface UserRepo extends JpaRepository<Users, String> {

    Users findByEmail(String email);
}
