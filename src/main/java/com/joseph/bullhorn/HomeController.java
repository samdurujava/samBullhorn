package com.joseph.bullhorn;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    MessageList list;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String showRegistrationPage(Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String processRegistrationPage(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        model.addAttribute("user", user);
        if (result.hasErrors()){
            return "registration";
        } else {
            user.setEnabled(true);
            user.setRoles(Arrays.asList(roleRepository.findByRole("USER")));
            userRepository.save(user);
            model.addAttribute("created",  true);
        }
        return "login";
    }

    @RequestMapping("/")
    public String homePage(Principal principal, Model model) {
        model.addAttribute("list", list.findAll());
        User user = ((CustomUserDetails)((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        model.addAttribute("user", user);
        return "list";
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/add")
    public String addMessage(Principal principal, Model model) {
        model.addAttribute("msg", new Message());
        User user = ((CustomUserDetails)((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        model.addAttribute("user", user);
        return "add";
    }

    @PostMapping("/send")
    public String sendMessage(Principal principal, @Valid Message msg, BindingResult result, @RequestParam("file")MultipartFile file) {
        if (result.hasErrors()) {
            return "redirect:/add";
        }
        if (!file.isEmpty()) {
            try {
                Map uploadResult = cloudc.upload(file.getBytes(), ObjectUtils.asMap("resourcetype", "auto"));
                if (msg.isSepia()) {
                    msg.setImage(cloudc.sepia(uploadResult.get("public_id").toString()));
                    String info = cloudc.sepiaThumb(uploadResult.get("public_id").toString() + ".jpg", 50, 50, "fill");
                    msg.setThumb(info);
                } else {
                    msg.setImage(uploadResult.get("url").toString());
                    String info = cloudc.createUrl(uploadResult.get("public_id").toString() + ".jpg", 50, 50, "fill");
                    String thumb = info.substring(info.indexOf("'") + 1, info.indexOf("'", info.indexOf("'") + 1));
                    msg.setThumb(thumb);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        User user = ((CustomUserDetails)((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        msg.setSentBy(user.getUsername());
        msg.setPostedDate(new Date());
        list.save(msg);

        return "redirect:/";
    }

    @RequestMapping("/view/{id}")
    public String viewTask(@PathVariable("id") long id, Model model) {
        model.addAttribute("msg", list.findById(id).get());
        return "show";
    }
}
