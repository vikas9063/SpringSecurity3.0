package com.vk.proj.controller;

import com.vk.proj.modal.Category;
import com.vk.proj.modal.Expense;
import com.vk.proj.modal.Roles;
import com.vk.proj.repo.ExpenseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.DateOperators;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.vk.proj.repo.UserRepo;
import com.vk.proj.modal.Users;

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


	private boolean isUserPresent(String userId) throws Exception {
		this.userRepo.findById(userId).orElseThrow(()-> new Exception("User not available"));
		return false;
	}

	@PostMapping("/auth/user")
	public ResponseEntity<Users> createUser(@RequestBody Users userRequest) throws Exception {
		if(userRepo.findByEmail(userRequest.getEmail()) != null){
			throw  new Exception("Email Already exists");
		}
		String password=userRequest.getPassword();
		List<Category> categories =  new ArrayList<>();
		categories.add(new Category("Fun","Spent money on Fun thing",""));
		categories.add(new Category("Movie","Spent money on Watching Movie",""));
		categories.add(new Category("Learning","Spent money on Learning new",""));
		userRequest.setCategories(categories);
		userRequest.setPassword(passwordEncoder.encode(password));

		Set<Roles> roles = new HashSet<>();
		roles.add(new Roles("NORMAL","Normal Role"));
		userRequest.setRoles(roles);
		return new ResponseEntity<Users>(userRepo.save(userRequest), HttpStatus.CREATED);
	}

	@GetMapping("/users")
	public ResponseEntity<List<Users>> getAllUsers(){
		return new ResponseEntity<>(userRepo.findAll(),HttpStatus.OK);
	}

	@GetMapping("/user/{email}")
	public ResponseEntity<Users> getUserByEmail(@PathVariable String email) throws Exception{
		 Users user= userRepo.findByEmail(email);

		 if(user == null) {
			 throw new Exception("User not exist");
		 }

		 return new ResponseEntity<>(user,HttpStatus.OK);
	}
	@GetMapping("/user-id/{id}")
	public ResponseEntity<Users> getUserById(@PathVariable String id) throws Exception{
		Users user = userRepo.findById(id).orElseThrow(
				() -> new Exception("User with given id not found on server !! : " + id));
		 
		 return ResponseEntity.ok().body(user);
	}

	@PutMapping("/user/{id}")
	public ResponseEntity<Users> updateUser(@RequestBody Users userRequest, @PathVariable String id) throws Exception{
		Users users = userRepo.findById(id).orElseThrow(
				()-> new Exception("User not found"+id));
		userRequest.setId(users.getId());
		userRequest.setEmail(users.getEmail());


		return  new ResponseEntity<>(userRepo.save(userRequest),HttpStatus.OK);
	}

	@PostMapping("user/add-expense")
	public ResponseEntity<Expense> createExpense(@RequestBody Expense expense) throws Exception {
		if((expense.getUserId() == null || expense.getUserId() =="") || isUserPresent(expense.getUserId())){
			throw new Exception("user Id is required");
		}
		expense.setExpId(UUID.randomUUID().toString());
		expense.setExpOn(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
		expense.setExpOnMonth(YearMonth.now().getMonthValue());
		expense.setExpOnYear(Year.now().getValue());
		expense.setExpOnDate(Integer.parseInt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd"))));
		//expense.setExpOnYear(new Date().getYear());

		return new ResponseEntity<>(this.expenseRepo.save(expense),HttpStatus.CREATED);
	}
	@GetMapping("/user/expense-all")
	public ResponseEntity<List<Expense>> grtAllExpense(){


		return null;
	}




}
