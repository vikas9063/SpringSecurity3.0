package com.vk.proj.controller;

import com.vk.proj.modal.*;
import com.vk.proj.repo.ExpenseRepo;
import com.vk.proj.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.*;

import com.vk.proj.repo.UserRepo;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/v1")
public class MainController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ExpenseRepo expenseRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ExpenseService expenseService;


    private boolean isUserPresent(String userId) throws Exception {
        this.userRepo.findById(userId).orElseThrow(() -> new Exception("User not available"));
        return true;
    }

    @PostMapping("/auth/user")
    public ResponseEntity<Users> createUser(@RequestBody Users userRequest) throws Exception {
        if (userRepo.findByEmail(userRequest.getEmail()) != null) {
            throw new Exception("Email Already exists");
        }
        String password = userRequest.getPassword();
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("Fun", "Spent money on Fun thing", ""));
        categories.add(new Category("Movie", "Spent money on Watching Movie", ""));
        categories.add(new Category("Learning", "Spent money on Learning new", ""));
        userRequest.setCategories(categories);
        userRequest.setPassword(passwordEncoder.encode(password));
        userRequest.setProfilePic("default.png");
        userRequest.setDailyLimit(1500.00);
        Set<Roles> roles = new HashSet<>();
        roles.add(new Roles("NORMAL", "Normal Role"));
        userRequest.setRoles(roles);
        return new ResponseEntity<Users>(userRepo.save(userRequest), HttpStatus.CREATED);
    }

    @GetMapping("/users")
    public ResponseEntity<List<Users>> getAllUsers() {
        return new ResponseEntity<>(userRepo.findAll(), HttpStatus.OK);
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<Users> getUserByEmail(@PathVariable String email) throws Exception {
        Users user = userRepo.findByEmail(email);

        if (user == null) {
            throw new Exception("User not exist");
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/user-id/{id}")
    public ResponseEntity<Users> getUserById(@PathVariable String id) throws Exception {
        Users user = userRepo.findById(id).orElseThrow(
                () -> new Exception("User with given id not found on server !! : " + id));

        return ResponseEntity.ok().body(user);
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<Users> updateUser(@RequestBody Users userRequest, @PathVariable String id) throws Exception {
        Users users = userRepo.findById(id).orElseThrow(
                () -> new Exception("User not found" + id));
        userRequest.setId(users.getId());
        userRequest.setEmail(users.getEmail());


        return new ResponseEntity<>(userRepo.save(userRequest), HttpStatus.OK);
    }

    @PostMapping("user/add-expense")
    public ResponseEntity<Expense> createExpense(@RequestBody Expense expense) throws Exception {
        if (expense.getUserId() == null || expense.getUserId() == "" || !isUserPresent(expense.getUserId())){
            throw new Exception("user Id is required");
        }
        expense.setExpId(UUID.randomUUID().toString());
        expense.setExpOn(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
        expense.setExpOnMonth(YearMonth.now().getMonthValue());
        expense.setExpOnYear(Year.now().getValue());
        expense.setExpOnDate(Integer.parseInt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd"))));
        //expense.setExpOnYear(new Date().getYear());

        return new ResponseEntity<>(this.expenseRepo.save(expense), HttpStatus.CREATED);
    }

    @GetMapping("/user/expense-all-paginated{userId}")
    public ResponseEntity<Map<String, Object>> grtAllExpensePaginated(@RequestParam(defaultValue = "0") Integer pageNo,
                                                                      @RequestParam(defaultValue = "5") Integer pageSize,
                                                                      @RequestParam(defaultValue = "id") String sortBy
    ) {

        Map<String, Object> map = new HashMap<>();
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));

        Page<Expense> pagedResult = expenseRepo.findAll(paging);

        map.put("totalPages", pagedResult.getTotalPages());
        map.put("totalResults", pagedResult.getTotalElements());
        map.put("currentPage", pagedResult.getNumber());
        map.put("noOfElements", pagedResult.getNumberOfElements());
        if (pagedResult.hasContent()) {
            map.put("resultData", pagedResult.getContent());
            return ResponseEntity.ok(map);
        } else {
            map.put("resultData", new ArrayList<Expense>());
            return ResponseEntity.ok(map);
        }

    }

    @GetMapping("/user/expenses/{userId}")
    public ResponseEntity<Map<String, Object>> getExpenseForUser(@PathVariable String userId,
                                                                 @RequestParam(defaultValue = "0") Integer pageNo,
                                                                 @RequestParam(defaultValue = "5") Integer pageSize,
                                                                 @RequestParam(defaultValue = "id") String sortBy
    ) {

        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));

        Page<Expense> pagedResult = expenseRepo.findAllByUserId(userId, paging);
        Map<String, Object> map = new HashMap<>();
        map.put("totalPages", pagedResult.getTotalPages());
        map.put("totalResults", pagedResult.getTotalElements());
        map.put("currentPage", pagedResult.getNumber());
        map.put("noOfElements", pagedResult.getNumberOfElements());
        if (pagedResult.hasContent()) {
            map.put("resultData", pagedResult.getContent());
            return ResponseEntity.ok(map);
        } else {
            map.put("resultData", new ArrayList<Expense>());
            return ResponseEntity.ok(map);
        }
    }


    @GetMapping("/user/expense-by-date/{userId}/{operation}")
    public ResponseEntity<Map<String,Object>> getExpenseByDate(@PathVariable String userId,
                                                               @PathVariable String operation,
                                                               @RequestParam() int year,
                                                               @RequestParam() int month

                                                               ){

        Map<String, Object> map= this.expenseService.expenseDependOnYear(year,month, userId,operation);

        return ResponseEntity.ok(map);
    }
    @PostMapping("/user/update-expense/{userId}/{expId}")
    public ResponseEntity<Map<String, Object>> updateExpense(@RequestBody Expense expense, @PathVariable String expId,
                                                             @PathVariable String userId
                                                             ) throws Exception {
        Expense exp=this.expenseRepo.findByExpId(expId);
        System.out.println("update"+exp.getUserId());
        if(exp == null){
            throw new Exception("Resource is not available to update");
        }
        if(exp.getUserId().equals(userId)){
            exp.setExpOnDate(expense.getExpOnDate());
            exp.setExpOnYear(expense.getExpOnYear());
            exp.setExpOn(expense.getExpOn());
            exp.setExpId(expId);
            exp.setExpCategory(expense.getExpCategory());
            exp.setExpDesc(expense.getExpDesc());
            exp.setExpTitle(expense.getExpTitle());
            exp.setExpOnMonth(expense.getExpOnMonth());
            exp.setPrice(expense.getPrice());
            exp.setUserId(userId);
            this.expenseRepo.save(exp);
        }else{
            throw new Exception("you can update only your expenses");
        }
        Map<String,Object> map = new HashMap<>();
        map.put("message","updated successfully");
        map.put("status","success");
        return ResponseEntity.ok(map);
    }

    @PostMapping("/user/update-profile/{userId}")
    public ResponseEntity<Map<String,Object>> updateProfile(@RequestParam MultipartFile file,
                                                            @PathVariable String userId
                                                            ) throws Exception {

        return ResponseEntity.ok(this.expenseService.updateProfile(file,userId));
    }

    @GetMapping("/user/load-profile/{userId}")
    public ResponseEntity<Resource> loadProfilePic(@PathVariable String userId) throws Exception {
       Users users= this.userRepo.findById(userId).orElseThrow(()-> new Exception("User not found"));
       String filename = users.getProfilePic();
        Resource file = this.expenseService.loadProfile(filename);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(MediaType.IMAGE_JPEG_VALUE))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping("/user/get-category/{userId}")
    public ResponseEntity<List<Category>> getCategories(@PathVariable String userId) throws Exception {
        List<Category> list = new ArrayList<>();
        Users users= this.userRepo.findById(userId).orElseThrow(()-> new Exception("User Not Found"));
        list = users.getCategories();
        return ResponseEntity.ok().body(list);
    }

    @PostMapping("/user/update-category/{userId}")
    public ResponseEntity<Map<String,Object>> updateCategory(@PathVariable String userId, @RequestBody List<Category> categories) throws Exception {
        System.out.println(categories.size());
        Users user=this.userRepo.findById(userId).orElseThrow(()-> new Exception("User not found "));
        int catSize= user.getCategories().size()+categories.size();
        if(catSize > 12){
            throw new Exception("One User can create only 12 categories");
        }
        categories.addAll(user.getCategories());
        user.setCategories(categories);
        user=this.userRepo.save(user);
        if(user == null){
            throw new Exception("category has not updated");
        }
        Map<String,Object> map = new HashMap<>();
        map.put("message","Category updated successfully");
        map.put("status","success");
        map.put("statusCode",HttpStatus.OK);
        return ResponseEntity.ok().body(map);
    }



    @GetMapping("/user/get-latest-expense/{userId}")
    public ResponseEntity<List<Expense>> getLatestExpenses(@PathVariable String userId) throws Exception {
        List<Expense> filteredList = new ArrayList<>();
        if(isUserPresent(userId)){
            List<Expense> expenses=this.expenseRepo.findAllByUserId(userId);

            int size=expenses.size();
           if(expenses != null && expenses.size()>0){
               for(int i=size-1; i>size-3; i--){
                   if(size == 1){
                      filteredList.add(expenses.get(i));
                      break;
                   }if(size >=2){
                       filteredList.add(expenses.get(i));
                   }
               }
           }
        }
        return ResponseEntity.ok().body(filteredList);
    }

    @GetMapping("/user/get-expend-yearly/{userId}/{year}")
    public ResponseEntity<Map<String, Object>> getExpendMonthly(
            @PathVariable String userId,
            @PathVariable int year
    ) throws Exception {
        Map<String,Object> map = null;
        if(isUserPresent(userId)) {
            map= this.expenseService.getUserExpenseCalculated(year,0,userId, "yearly");
            map.put("year",year);
        }
        return ResponseEntity.ok().body(map);
    }
    @GetMapping("/user/get-expend-monthly/{userId}/{year}/{month}")
    public ResponseEntity<Map<String, Object>> getExpendYearly(
            @PathVariable String userId,
            @PathVariable int year,
            @PathVariable int month
    ) throws Exception {
        Map<String,Object> map = new HashMap<>();
        if(isUserPresent(userId)){
            map=this.expenseService.getUserExpenseCalculated(year,month,userId,"monthly");
            map.put("year",year);
            map.put("month",month);
        }
        return ResponseEntity.ok().body(map);
    }
    @GetMapping("/user/get-expend-today/{userId}/{dateReq}")
    public ResponseEntity<Map<String,Object>> getExpendToday(@PathVariable String userId,
                                                              @PathVariable String dateReq
                                                              ) throws Exception {
        String[] dSplit = dateReq.split("-");
        int year = Integer.parseInt(dSplit[0]);
        int month = Integer.parseInt(dSplit[1]);
        int date=Integer.parseInt(dSplit[2]);
        List<Expense> expenses=new ArrayList<>();
        Map<String,Object> map=new HashMap<>();
        double total=0;
        if(isUserPresent(userId)){
            List<String> categories = new ArrayList<>();

            expenses=this.expenseRepo.findAllByUserIdAndExpOnYearAndExpOnMonthAndExpOnDate(userId,year,month,date);
            List<ExpenseResponse> filteredExp = new ArrayList<>();
            if(expenses != null && expenses.size()>0){
               for(Expense e:expenses){
                   System.out.println(e.getPrice());
                   if(!categories.contains(e.getExpCategory())){
                       categories.add(e.getExpCategory());
                   }
               }
                for(String c: categories){
                    double price= 0;
                    String title="";
                    String catTitle = c.trim();
                    //System.out.println(catTitle+"<<<<<<<<<");
                    for(Expense e: expenses){
                        //System.out.println(e.getExpTitle()+"<<<<>>>>>"+catTitle);
                        if(e.getExpCategory().trim().equals(catTitle)){
                           System.out.println("<<<<< In IF");
                            System.out.println(e.getPrice());
                            price = price+e.getPrice();
                            title = title + " | "+e.getExpTitle();
                        }
                    }
                    total=total+price;
                    ExpenseResponse expenseResponse = new ExpenseResponse();
                    expenseResponse.setDate(date);
                    expenseResponse.setYear(year);
                    expenseResponse.setMonth(month);
                    expenseResponse.setTitle(title);
                    expenseResponse.setPrice(price);
                    expenseResponse.setOperationFlag("Daily");
                    expenseResponse.setExpCategory(catTitle);
                    filteredExp.add(expenseResponse);
                }
                         }
            map.put("resultData",filteredExp);
            map.put("dailyTotal",total);
            map.put("status","success");
            map.put("message","Fetched Successfully");
            map.put("date",date);
            map.put("year",year);
            map.put("month",month);
        }

        return ResponseEntity.ok().body(map);
    }

    @DeleteMapping("/user/delete/{userId}/{catName}")
    public ResponseEntity<Map<String,Object>> deleteCategory(@PathVariable String userId, @PathVariable String catName) throws Exception {

        Users user = this.userRepo.findById(userId).orElseThrow(()->new Exception("User Not Found"));
        List<Category> categories = user.getCategories();
        List<Category> filteredCategory = new ArrayList<>();
        for(Category c:categories){
            if(!c.getCatName().trim().equals(catName)){
                filteredCategory.add(c);
            }
        }
        user.setCategories(filteredCategory);
        user=userRepo.save(user);
        if(user == null){
            throw new Exception("Category not updated");
        }
        Map<String,Object> map = new HashMap<>();
        map.put("message","Category deleted successfully");
        map.put("status","success");
        map.put("statusCode",HttpStatus.OK);
        return ResponseEntity.ok().body(map);
    }

    @GetMapping("/user/expense-user-paginated/{userId}/{date}/{operation}")
    public ResponseEntity<Map<String,Object>> getUserExpensePaginated(@PathVariable String userId,
                                                                      @PathVariable String operation,
                                                                      @PathVariable String date,
                                                                      @RequestParam(defaultValue = "0") Integer pageNo,
                                                                      @RequestParam(defaultValue = "5") Integer pageSize,
                                                                      @RequestParam(defaultValue = "id") String sortBy
                                                                      ) throws Exception {

        Map<String,Object> map=null;
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        System.out.println(operation);
        if(isUserPresent(userId)){
             map=this.expenseService.getUserExpensePaginated(paging,userId,date,operation);
        }
        map.put("status","success");
        return ResponseEntity.ok().body(map);
    }


    @DeleteMapping("/user/delete-expense/{expId}/{userId}")
    public ResponseEntity<Map<String,Object>> deleteExpense(@PathVariable String expId,
                                                            @PathVariable String userId) throws Exception {
        Map<String,Object> map = new HashMap<>();
        if(isUserPresent(userId)){

            Expense expense= this.expenseRepo.findByExpId(expId);
            if(expense == null){
                throw new Exception("Expense not available to delete");
            }
            if(!userId.equals(expense.getUserId())){
                throw new Exception("you can delete only yours expenses");
            }
            this.expenseRepo.deleteByExpId(expId);
        }
        map.put("status","success");
        map.put("message","Expense deleted successfully");
        return ResponseEntity.ok().body(map);
    }

}
