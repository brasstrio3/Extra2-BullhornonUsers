package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    MessageRepository messageRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String messages(Model model) {
        model.addAttribute("messages", messageRepository.findAll());
        return "listmessages";
    }

    @GetMapping("/add")
    public String addMessage(Model model) {
        System.out.println(new Message());
        model.addAttribute("messageobject", new Message());
        return "addMessage";
    }

    @PostMapping("/process")
    public String processForm(@Valid @ModelAttribute("messageobject") Message message, BindingResult result) {
        if (result.hasErrors()) {
            return "addMessage";
        }
        messageRepository.save(message);
        return "redirect:/";
    }

    @PostMapping("/add")
    public String profilePic(@ModelAttribute Message message, @RequestParam("file")MultipartFile file) {
        if (file.isEmpty()) {
            return "addMessage";
        }
        try {
            Map uploadResult = cloudc.upload(file.getBytes(), ObjectUtils.asMap("resourcetype", "auto"));
            System.out.println(uploadResult.get("url").toString());
            message.setHeadshot(uploadResult.get("url").toString());
            messageRepository.save(message);
        } catch (IOException e) {
            e.printStackTrace();
            return "addMessage";
        }
        return "redirect:/";
    }
}
