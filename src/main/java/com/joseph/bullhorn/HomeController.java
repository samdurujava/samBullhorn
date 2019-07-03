package com.joseph.bullhorn;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Date;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    MessageList list;

    @Autowired
    UserList users;

    User currentUser = null;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String homePage(Model model) {
        if (currentUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("list", list.findAll());
        model.addAttribute("user", currentUser);
        return "list";
    }

    @GetMapping("/login")
    public String login(Model model) {
        if (currentUser != null) {
            return "redirect:/";
        }
        model.addAttribute("user", new User());
        return "login";
    }

    @RequestMapping("/logout")
    public String logout() {
        currentUser = null;
        return "redirect:/login";
    }

    @PostMapping("/enter")
    public String loginEnter(@Valid User user, BindingResult result) {
        if (result.hasErrors()) {
            return "login";
        }
        boolean notExist = true;
        boolean nameTaken = false;
        for (User prevUser : users.findAll()) {
            if (prevUser.getUsername().equals(user.getUsername()) && prevUser.getPassword().equals(user.getPassword())) {
                notExist = false;
            } else if (prevUser.getUsername().equals(user.getUsername())) {
                nameTaken = true;
            }
        }
        if (notExist && nameTaken) {
            return "login";
        } else if (notExist) {
            users.save(user);
        }
        currentUser = user;
        return "redirect:/";
    }

    @GetMapping("/add")
    public String addMessage(Model model) {
        if (currentUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("msg", new Message());
        return "add";
    }

    @PostMapping("/send")
    public String sendMessage(@Valid Message msg, BindingResult result, @RequestParam("file")MultipartFile file) {
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
        msg.setSentBy(currentUser.getUsername());
        msg.setPostedDate(new Date());
        list.save(msg);

        return "redirect:/";
    }

    @RequestMapping("/view/{id}")
    public String viewTask(@PathVariable("id") long id, Model model) {
        if (currentUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("msg", list.findById(id).get());
        return "show";
    }
}
