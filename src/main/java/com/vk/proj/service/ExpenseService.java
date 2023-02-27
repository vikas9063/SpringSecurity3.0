package com.vk.proj.service;

import com.vk.proj.modal.Expense;
import com.vk.proj.modal.ExpenseResponse;
import com.vk.proj.modal.Users;
import com.vk.proj.repo.ExpenseRepo;
import com.vk.proj.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.*;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepo expenseRepo;

    @Autowired
    private UserRepo userRepo;
    private final Path root = Paths.get("src\\main\\resources\\static");

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
            String fileExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            System.out.println(fileExtension);
            ArrayList list = new ArrayList();
            list.add("jpeg");
            list.add("jpg");
            list.add("png");
            if(!list.contains(fileExtension)){
                throw new Exception("please provide image in JPEG or png format");
            }


            String fileName = userId+"_"+users.getEmail()+"."+ fileExtension;
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

    // yearly user spends

    public Map<String,Object> getUserExpenseCalculated(int year, int month, String userId, String operation){
        Map<String,Object> expenses=expenseDependOnYear(year,month,userId,operation);
        List<Expense> expenseList= (List<Expense>) expenses.get("resultData");
        List<Expense> filteredList = new ArrayList<>();
        if(operation.equals("monthly")) {
            return getExpenseMonthly(expenseList, year,month);
        }
         return getExpenseYearly(expenseList, year);

    }
    private Map<String,Object> getExpenseYearly(List<Expense> expenses,int year){
        List<ExpenseResponse> filteredExpenses =  new ArrayList<>();
        int[] months = {1,2,3,4,5,6,7,8,9,10,11,12};
        double total=0;
        if(expenses != null && expenses.size()>0){
           for(int i=0; i<months.length;i++){
               double price=0;
               String title="";
               int month = months[i];
               for(Expense e:expenses){
                   if(e.getExpOnMonth() == month){
                       price=price+e.getPrice();
                       title = title + " | "+e.getExpTitle();
                   }
               }
               total=total+price;
               System.out.println(price+"<<<<>>>>"+month);
               ExpenseResponse expenseResponse =  new ExpenseResponse();
                expenseResponse.setMonth(month);
                expenseResponse.setPrice(price);
                expenseResponse.setOperationFlag("yearly");
                expenseResponse.setYear(year);
                expenseResponse.setTitle(title);
                filteredExpenses.add(expenseResponse);
           }
        }
        Map<String,Object> map = new HashMap<>();
        map.put("resultData",filteredExpenses);
        map.put("yearlyTotal",total);
        map.put("status","success");
        map.put("message","Fetched Successfully");
        return map;
    }

    private Map<String,Object> getExpenseMonthly(List<Expense> expenses, int year, int month){
        System.out.println("monthly");
        List<ExpenseResponse> expenseRepoList = new ArrayList<>();
        int[] days = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31};
        double total=0;
        for(int i=0; i<days.length;i++){

            double price=0;
            String title="";
            int day = days[i];
            for(Expense e:expenses){
                if(e.getExpOnDate() == day){
                    price=price+e.getPrice();
                    title = title + " | "+e.getExpTitle();
                }
            }
            total = total+price;
            ExpenseResponse expenseResponse =  new ExpenseResponse();
            expenseResponse.setMonth(month);
            expenseResponse.setPrice(price);
            expenseResponse.setOperationFlag("monthly");
            expenseResponse.setYear(year);
            expenseResponse.setTitle(title);
            expenseResponse.setDate(day);
            expenseRepoList.add(expenseResponse);
        }
        Map<String,Object> map = new HashMap<>();
        map.put("resultData",expenseRepoList);
        map.put("monthlyTotal",total);
        map.put("status","success");
        map.put("message","Fetched Successfully");
        return map;
    }
    public Map<String,Object> getUserExpensePaginated(Pageable paging,String userId,String dateReq,String operation){
        String[] dateReqArr= dateReq.split("-");
        int day=Integer.parseInt(dateReqArr[2]);
        int month=Integer.parseInt(dateReqArr[1]);
        int year=Integer.parseInt(dateReqArr[0]);
        Map<String,Object> map = new HashMap<>();
        if(operation.equals("daily")){
            System.out.println("Daily....");
            Page<Expense> page = this.expenseRepo.findAllByUserIdAndExpOnYearAndExpOnMonthAndExpOnDate(userId,year,month,day,paging);
            map.put("totalPages", page.getTotalPages());
            map.put("totalResults", page.getTotalElements());
            map.put("currentPage", page.getNumber());
            map.put("noOfElements", page.getNumberOfElements());
            map.put("year",year);
            map.put("month",month);
            map.put("date",day);
            map.put("isLast",page.isLast());
            map.put("isFirst",page.isFirst());
            if (page.hasContent()) {
                map.put("resultData", page.getContent());
            } else {
                map.put("resultData", new ArrayList<Expense>());
            }
        }else if(operation.equals("monthly")){
            System.out.println("Monthly");
            Page<Expense> page = this.expenseRepo.findAllByUserIdAndExpOnYearAndExpOnMonth(userId,year,month,paging);
            map.put("totalPages", page.getTotalPages());
            map.put("totalResults", page.getTotalElements());
            map.put("currentPage", page.getNumber());
            map.put("noOfElements", page.getNumberOfElements());
            map.put("year",year);
            map.put("month",month);
            map.put("isLast",page.isLast());
            map.put("isFirst",page.isFirst());
            if (page.hasContent()) {
                map.put("resultData", page.getContent());
            } else {
                map.put("resultData", new ArrayList<Expense>());
            }
        }else if(operation.equals("yearly")){
            System.out.println("Yearly");
            Page<Expense> page = this.expenseRepo.findAllByUserIdAndExpOnYear(userId,year,paging);
            map.put("totalPages", page.getTotalPages());
            map.put("totalResults", page.getTotalElements());
            map.put("currentPage", page.getNumber());
            map.put("noOfElements", page.getNumberOfElements());
            map.put("isLast",page.isLast());
            map.put("isFirst",page.isFirst());
            map.put("year",year);
            if (page.hasContent()) {
                map.put("resultData", page.getContent());
            } else {
                map.put("resultData", new ArrayList<Expense>());
            }
        }


        return map;
    }

}
