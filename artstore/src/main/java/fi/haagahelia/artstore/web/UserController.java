package fi.haagahelia.artstore.web;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import fi.haagahelia.artstore.domain.*;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class UserController {


    private final AppUserRepository repository;

   

    public UserController(AppUserRepository appUserRepository) {
        this.repository = appUserRepository;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", authentication.getAuthorities().iterator().next().getAuthority());
        return "profile";
    }

    @GetMapping(value = "/signup")
    public String addUser(Model model) {
        model.addAttribute("signupform", new SignUpForm());
        return "signup";
    }
    
    
     @PostMapping(value = "/saveuser")
    public String save(@Validated @ModelAttribute("signupform") SignUpForm signupForm, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            if (signupForm.getPassword().equals(signupForm.getPasswordCheck())) {
                String pwd = signupForm.getPassword();
                BCryptPasswordEncoder bc = new BCryptPasswordEncoder();
                String hashPwd = bc.encode(pwd);

                AppUser newUser = new AppUser();
                newUser.setPasswordHash(hashPwd);
                newUser.setUsername(signupForm.getUsername());
                newUser.setRole("USER");
                if (repository.findByUsername(signupForm.getUsername()) == null) {
                    repository.save(newUser);
                } else {
                    bindingResult.rejectValue("username", "err.username", "Username already exists");
                    return "signup";
                }
            } else {
                bindingResult.rejectValue("passwordCheck", "err.passCheck", "Passwords does not match");
                return "signup";
            }
        } else {
            return "signup";
        }
        return "redirect:/home";
    }

}
