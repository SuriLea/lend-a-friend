package com.wildcodeschool.lendafriend.controller;

import com.wildcodeschool.lendafriend.entity.ObjectBorrowed;
import com.wildcodeschool.lendafriend.entity.ObjectLended;
import com.wildcodeschool.lendafriend.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.wildcodeschool.lendafriend.repository.ObjectBorrowedRepository;
import com.wildcodeschool.lendafriend.repository.ObjectLendedRepository;
import com.wildcodeschool.lendafriend.repository.UserRepository;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Controller
public class MainController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectBorrowedRepository borrowedRepository;

    @Autowired
    ObjectLendedRepository lendedRepository;

    @GetMapping("/")
    public String getIndex() {

        return "index";
    }

    @GetMapping("/inscription")
    public String getRegistration() {

        return "register";
    }

    @PostMapping("/inscription")
    public String postRegistration(Model out,
                                   @RequestParam(required = false) String username, @RequestParam String password,
                                   @RequestParam String passwordValidation, @ModelAttribute User user) {

        if (!password.equals(passwordValidation)) {
            out.addAttribute("valid", false);
            return "register";
        }

        if (username.isEmpty()) {
            out.addAttribute("emptyUsername", true);
            return "register";
        }

        if (userRepository.findAll().contains(username)) {
            out.addAttribute("userAlreadyExists", true);
            return "register";
        }

        user.setUsername(username);
        user.setPassword(password);
        userRepository.save(user);
        out.addAttribute("userCreated", true);
        return "register";
    }

    @PostMapping("/")
    public String postLogin(@RequestParam String username, @RequestParam String password, HttpSession session, Model out) {

        if (userRepository.findByUsernameAndPassword(username, password).isPresent()) {
            User user = userRepository.findByUsernameAndPassword(username, password).get();
            session.setAttribute("user", user);
            out.addAttribute("username", user.getUsername());
            List<ObjectBorrowed> borrowedObjects = borrowedRepository.findAllByUserObjectBorrowed(user);
            out.addAttribute("objectsBorrowed", borrowedObjects);
            return "home";
        }

        out.addAttribute("invalidUser", false);
        return "index";
    }


    //TODO: @PostMapping("/home") public String postHome() {return: home + signal visuel d'update}

    @GetMapping("/disconnect")
    public String getDisconnected(HttpSession session) {

        session.removeAttribute("user");
        return "redirect:/";
    }
}
