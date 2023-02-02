package com.vk.proj.service;

import com.vk.proj.modal.Expense;
import com.vk.proj.modal.Users;
import com.vk.proj.repo.ExpenseRepo;
import com.vk.proj.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepo expenseRepo;

    @Autowired
    private UserRepo userRepo;
    private final Path root = Paths.get("C:\\uploads");

    public void init() throws Exception {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new Exception("Could not initialize folder for upload!");
        }
    }

    public Map<String, Object> expenseDependOnYear(int year, int month, String userId, String operation) {

        Map<String, Object> map = new HashMap<>();

        System.out.println(">>>> Operation Yearly <<<<" + year);
        List<Expense> page = this.expenseRepo.findAllByExpOnYearAndUserId(year, userId);
        List<Expense> expenses = null;
        System.out.println(">>>> Operation Yearly <<<<" + page.size());
        if (page != null && page.size() > 0) {
            List<Expense> totalExpense = page;

            if (operation.equals("yearly")) {
                System.out.println(">>>> Operation Yearly <<<<");
                expenses = new ArrayList<>();
                for (Expense e : totalExpense) {
                    if (e.getExpOnYear() == year) {
                        expenses.add(e);
                    }
                }
            } else {
                System.out.println(">>>> Operation else <<<<");
                expenses = new ArrayList<>();
                for (Expense e : totalExpense) {
                    if (e.getExpOnYear() == year && e.getExpOnMonth() == month) {
                        expenses.add(e);
                    }
                }
            }
        } else {
            expenses = new ArrayList<>();
        }
        map.put("resultData", expenses);
        return map;
    }


    public Map<String, Object> updateProfile(MultipartFile file, String userId) throws Exception {
        try {
            Users users= this.userRepo.findById(userId).orElseThrow(()-> new Exception("User Not Found !!"));
            String fileName = userId+"_"+users.getEmail()+"."+ StringUtils.getFilenameExtension(file.getOriginalFilename());
            users.setProfilePic(fileName);
            Files.copy(file.getInputStream(), this.root.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            this.userRepo.save(users);
        } catch (Exception e) {
            if (e instanceof FileAlreadyExistsException) {
                throw new Exception("A file of that name already exists.");
            }

            throw new Exception(e.getMessage());
        }
        Map map = new HashMap<String, Object>();
        map.put("message", "profile Updated Successfully");
        return map;
    }


    public Resource  loadProfile(String filename) {

        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }



    }
}
