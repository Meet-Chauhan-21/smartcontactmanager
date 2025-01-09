package com.smart.controller;

import com.smart.entity.User;
import com.smart.helper.Message;
import com.smart.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class HomeController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/")
    public String home(Model model){
        model.addAttribute("title","Home Page - Smart Contact Manager");
        return "home";
    }

    @RequestMapping("/about")
    public String about(Model model){
        model.addAttribute("title","About page - Smart Contact Manager");
        return "about";
    }

    @RequestMapping("/signup")
    public String signup(Model model){
        model.addAttribute("title","Signup page - Smart Contact Manager");
        model.addAttribute("user",new User());
        return "signup";
    }

    //handler for registering user
    @RequestMapping(value = "/do_register", method = RequestMethod.POST)
    public String registerUser(@ModelAttribute("user") User user , @RequestParam(value = "agreement",defaultValue = "false") boolean agreement , Model model, HttpSession session){

        try{

            if (!agreement){
                System.out.println("You have a not agreed terms and conditions.");
                throw new Exception("You have a not agreed terms and conditions.");
            }

            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("default.png");
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            System.out.println("Agreement : "+ agreement);
            System.out.println("USER : "+ user);

            User result = userRepository.save(user);

            model.addAttribute("user",new User());

            session.setAttribute("message", new Message("successfully registered","alert-success"));
            return "signup";

        } catch (Exception e) {

            e.printStackTrace();
            model.addAttribute("user",user);
            session.setAttribute("message", new Message("something went wrong."+e.getMessage(),"alert-danger"));
            return "signup";

        }

    }

    @GetMapping("/signin")
    public String customLogin(Model model){
        model.addAttribute("title","Login Page - Smart Contact Manger.");
        return "login";
    }


}
