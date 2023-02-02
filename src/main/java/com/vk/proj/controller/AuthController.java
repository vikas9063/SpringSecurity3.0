package com.vk.proj.controller;

import com.vk.proj.config.JwtTokenHelper;
import com.vk.proj.modal.LoginReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class AuthController {


    @Autowired
    private JwtTokenHelper jwtTokenHelper;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/auth/login")
    public ResponseEntity<Map<String,Object>> login(@RequestBody LoginReq loginReq) throws Exception{
        System.out.println("started !!!"+loginReq.getUsername()+loginReq.getPassword());
        this.authenticate(loginReq.getUsername(), loginReq.getPassword());
        System.out.println("after Auth");
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(loginReq.getUsername());
        System.out.println("after userDetailsService");
        String token = this.jwtTokenHelper.generateToken(userDetails);
        System.out.println("after generateToken");
        Map<String,Object> map = new HashMap();
        map.put("token", token);
        map.put("tokenExpTime",this.jwtTokenHelper.getExpirationDateFromToken(token).toString());
        map.put("message","Success");
        map.put("userId",userDetails.getUsername());
        map.put("userRole",userDetails.getAuthorities());


        return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
    }

    private void authenticate(String username, String password) throws Exception {

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
                password);
        try {
            this.authenticationManager.authenticate(authenticationToken);
        } catch (DisabledException e) {

            throw new DisabledException("User is Disabled");
        } catch (BadCredentialsException e) {

            throw new BadCredentialsException("Bad Credentials");
        }
    }

}
