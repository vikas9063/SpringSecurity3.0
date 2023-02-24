package com.vk.proj.repo;

import com.vk.proj.modal.Category;
import com.vk.proj.modal.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepo extends JpaRepository<Category,String> {


}
