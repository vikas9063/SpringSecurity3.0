package com.vk.proj.repo;

import com.vk.proj.modal.Expense;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface ExpenseRepo extends MongoRepository<Expense,String> {
    Page<Expense> findAllByUserId(String userId, Pageable pageable);

    List<Expense> findAllByUserId(String userId);

    Expense findByExpId(String expId);

    List<Expense> findAllByExpOnYearAndUserId(int year,String userId);

    List<Expense> findAllByUserIdAndExpOnYearAndExpOnMonthAndExpOnDate(String userId,int year,int month,int date);

    Page<Expense> findAllByExpOnDateAndExpOnMonthAndExpOnYear(int date, int month, int year, Pageable pageable);

    Page<Expense> findAllByUserIdAndExpOnYearAndExpOnMonthAndExpOnDate(String userId,int year,int month,int date,Pageable pageable);

    Page<Expense> findAllByUserIdAndExpOnYearAndExpOnMonth(String userId,int year,int month,Pageable pageable);

    Page<Expense> findAllByUserIdAndExpOnYear(String userId,int year,Pageable pageable);

    void deleteByExpId(String expId);


}
