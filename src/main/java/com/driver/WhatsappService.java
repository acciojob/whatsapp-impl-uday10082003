package com.driver;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class WhatsappService {

    WhatsappRepository whatsappRepository = new WhatsappRepository();

    public String createUser(String name, String mobile) {
        String msg;
        try{
            msg = whatsappRepository.createUser(name, mobile);
        }
        catch (UserAlreadyExistsException e){
            return e.toString();
        }
        return msg;
    }

    public Group createGroup(List<User> users) {
        return whatsappRepository.createGroup(users);
    }

    public int createMessage(String content) {
        return whatsappRepository.createMessage(content);
    }

    public int sendMessage(Message message, User sender, Group group) {
        try{
            return whatsappRepository.sendMessage(message, sender, group);
        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public String changeAdmin(User approver, User user, Group group) {
        try{
            return whatsappRepository.changeAdmin(approver, user, group);
        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public int removeUser(User user) {
        try{
            return whatsappRepository.removeUser(user);
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public String findMessage(Date start, Date end, int k) {
        try{
            return whatsappRepository.findMessage(start, end, k);
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}