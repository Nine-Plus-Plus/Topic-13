package com.project.service;

import com.project.dto.EmailRequest;
import com.project.dto.Response;
import com.project.exception.OurException;
import com.project.model.EmailDetails;
import com.project.repository.GroupRepository;
import com.project.repository.UsersRepository;
import com.project.ultis.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class EmailServiceImpl {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private AuthService authService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupRepository groupRepository;

    @Value("${spring.mail.username}")
    private String sender;

    public Response sendSimpleMail(EmailRequest emailRequest){
        Response response = new Response();
        try{
            SimpleMailMessage mailMessage = new SimpleMailMessage();

            mailMessage.setFrom(sender);
            mailMessage.setTo(emailRequest.getRecipient());
            mailMessage.setText(emailRequest.getMsgBody());
            mailMessage.setSubject(emailRequest.getSubject());

            javaMailSender.send(mailMessage);
            response.setStatusCode(200);
            response.setMessage("Sending email to: " + emailRequest.getRecipient() + " successfully");

        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred sending mail: " + e.getMessage());
        }
        return response;
    }
}
