package com.vk.proj.repo;

import com.vk.proj.modal.Expense;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface ExpenseRepo extends MongoRepository<Expense,String> {
    Page<Expense> findAllByUserId(String userId, Pageable pageable);

    Expense findByExpId(String expId);

    List<Expense> findAllByExpOnYearAndUserId(int year,String userId);

    Page<Expense> findAllByExpOnDateAndExpOnMonthAndExpOnYear(int date, int month, int year, Pageable pageable);

}
