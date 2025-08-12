package com.smart.controller;

import com.smart.entity.Contact;
import com.smart.entity.User;
import com.smart.helper.Message;
import com.smart.repository.ContactRepository;
import com.smart.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;


    //method for adding common data to response
    @ModelAttribute
    public void addCommonData(Model model , Principal principal){
        String userName = principal.getName();
        System.out.println("USERNAME IS : "+ userName);

        // Get the user using username(Email) ---

        User user = userRepository.getUserByUserName(userName);
        System.out.println("USER IS : "+ user);

        model.addAttribute("user",user);

    }

    // dashboard home
    @RequestMapping("/index")
    public String dashboard(Model model , Principal principal){

        model.addAttribute("title","User Dashboard");
        return "normal/user_dashboard";
    }

    // open add form handler ----

    @GetMapping("/add-contact")
    public String openAddContactForm(Model model){

        model.addAttribute("title","Add Contact");
        model.addAttribute("contact",new Contact());

        return "normal/add_contact_form";
    }

    // processing add contact form

    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact,
                                 @RequestParam("profileImage") MultipartFile file,
                                 Principal principal,
                                 HttpSession session ){

        try {

            String name = principal.getName();
            User user = userRepository.getUserByUserName(name);

            // processing and uploading file ----
            if (file.isEmpty()){
                // to print message for empty file
                System.out.println("File is Empty.");
                contact.setImage("contact.png");

            } else {
                // to upload the file
                contact.setImage(file.getOriginalFilename());

                File saveFile = new ClassPathResource("static/css/image").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

                Files.copy(file.getInputStream(), path , StandardCopyOption.REPLACE_EXISTING);

                System.out.println("Image is uploaded");

            }

            contact.setUser(user);
            user.getContact().add(contact);
            userRepository.save(user);

            System.out.println("Data : "+ contact);
            System.out.println("Added to database.");

            // message success
            session.setAttribute("message",new Message("Your contact is added ! Add More .","success"));

        } catch (Exception e) {
            System.out.println("ERROR : " + e.getMessage());
            e.printStackTrace();

            // message error
            session.setAttribute("message",new Message("some went wrong ! Try again .","danger"));
        }

        return "normal/add_contact_form";
    }

    // show contacts handler
    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page , Model m, Principal principal){
        m.addAttribute("title","Show User Contacts");

        String userName = principal.getName();

        User user = userRepository.getUserByUserName(userName);

        Pageable pageable = PageRequest.of(page, 10);

        Page<Contact> contacts = contactRepository.findContactsByUser(user.getId(),pageable);

        m.addAttribute("contacts", contacts);
        m.addAttribute("currentPage",page);
        m.addAttribute("totalPages",contacts.getTotalPages());

        return "normal/show_contacts";
    }

    // Showing specific contact details

    @RequestMapping("/{cId}/contact")
    public String showContactDetail(@PathVariable("cId") Integer cId, Model m, Principal principal){
        System.out.println("CID : "+ cId);

        Optional<Contact> optionalContact = contactRepository.findById(cId);
        Contact contact = optionalContact.get();

        String userName = principal.getName();
        User user = userRepository.getUserByUserName(userName);

        if(user.getId() == contact.getUser().getId()){
            m.addAttribute("contact", contact);
            m.addAttribute("title",contact.getName());
        }

        return "normal/contact_detail";
    }

    // Delete Contact Handler

    @GetMapping("/delete/{cId}")
    public String deleteContact(@PathVariable("cId") Integer cId,
                                Model model,
                                Principal principal,
                                HttpSession session ){

        String userName = principal.getName();
        User user = userRepository.getUserByUserName(userName);

        Contact contact = contactRepository.findById(cId).get();

        if(user.getId() == contact.getUser().getId()) {

            user.getContact().remove(contact);
            userRepository.save(user);

        }

        return "redirect:/user/show-contacts/0";
    }

    // Update Form Handler

    @PostMapping("/update-contact/{cid}")
    public String updateForm(@PathVariable("cid") Integer cid,Model m){

        m.addAttribute("title","Update Contact");

        Contact contact = contactRepository.findById(cid).get();
        m.addAttribute("contact", contact);

        return "normal/update_form";
    }

    // Update Contact Handler

    @RequestMapping(value = "/process-update" , method = RequestMethod.POST)
    public String updateHandler(@ModelAttribute Contact contact,
                                @RequestParam("profileImage") MultipartFile file,
                                HttpSession session,
                                Model m,
                                Principal principal){

        Contact oldcontactDetail = contactRepository.findById(contact.getcId()).get();

        try {

            if (!file.isEmpty()){

                // delete old photo

                File deleteFile = new ClassPathResource("static/css/image").getFile();
                File file1 = new File(deleteFile,oldcontactDetail.getImage());
                file1.delete();

                // update new photo

                File saveFile = new ClassPathResource("static/css/image").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

                Files.copy(file.getInputStream(), path , StandardCopyOption.REPLACE_EXISTING);

                contact.setImage(file.getOriginalFilename());


            } else {

                contact.setImage(oldcontactDetail.getImage());

            }

            User user = userRepository.getUserByUserName(principal.getName());
            contact.setUser(user);
            contactRepository.save(contact);

            session.setAttribute("message",new Message("Your Contact is updated ..!","success"));

        } catch (Exception e) {
            e.printStackTrace();

        }

        System.out.println("CONTACT NAME : "+ contact.getName());
        System.out.println("CONTACT EMAIL : "+ contact.getEmail());
        System.out.println("CONTACT ID : "+ contact.getcId());

        return "redirect:/user/"+contact.getcId()+"/contact";
    }

    //Your Profile Handler

    @GetMapping("/profile")
    public String yourProfile(Model model){

        model.addAttribute("title","Profile Page");

        return "normal/profile";
    }

    // user about page

    @GetMapping("/user-about")
    public String myAbout(Model model){

        model.addAttribute("title","User About Page");

        return "normal/myabout";
    }


}
