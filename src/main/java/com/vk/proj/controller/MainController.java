package com.vk.proj.controller;

import com.vk.proj.modal.Category;
import com.vk.proj.modal.Expense;
import com.vk.proj.modal.Roles;
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
import com.vk.proj.modal.Users;
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
        return false;
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
        if ((expense.getUserId() == null || expense.getUserId() == "") || isUserPresent(expense.getUserId())) {
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


}
