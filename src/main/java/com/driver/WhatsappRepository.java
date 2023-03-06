package com.driver;


import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private LinkedHashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private int customGroupCount;
    private int messageId;
    private HashMap<String, User> mobileUserMap;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new LinkedHashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;
        this.mobileUserMap = new HashMap<String, User>();
    }

    public String createUser(String name, String mobile) throws UserAlreadyExistsException {
        if (mobileUserMap.containsKey(mobile)){
            throw new UserAlreadyExistsException("User already exists");
        }
        User user = new User(name, mobile);
        mobileUserMap.put(mobile, user);
        return "SUCCESS";
    }

    public Group createGroup(List<User> users) {
        Group group;
        if (users.size() > 2){
            customGroupCount++;
            group = new Group("Group "+customGroupCount, users.size());
            groupUserMap.put(group, users);
            adminMap.put(group, users.get(0));
            return group;
        }
        group = new Group(users.get(users.size()-1).getName(), users.size());
        groupUserMap.put(group, users);
        adminMap.put(group, users.get(0));
        return group;
    }

    public int createMessage(String content) {
        messageId++;
        Message message = new Message(messageId, content);
        return message.getId();
    }

    public int sendMessage(Message message, User sender, Group group) {

        if (groupUserMap.containsKey(group)){
            if (groupUserMap.get(group).contains(sender)){
                List<Message> messages = new ArrayList<>();
                if (groupMessageMap.containsKey(group)){
                    messages = groupMessageMap.get(group);
                    messages.add(message);
                    groupMessageMap.put(group, messages);
                }
                else{
                    messages.add(message);
                    groupMessageMap.put(group, messages);
                }
                senderMap.put(message, sender);
                return messages.size();
            }
            else{
                throw new RuntimeException("You are not allowed to send message");
            }
        }
        else{
            throw new RuntimeException("Group does not exist");
        }
    }

    public String changeAdmin(User approver, User user, Group group) {
        if (adminMap.containsKey(group)){
            if (adminMap.get(group).equals(approver)){
                if (groupUserMap.get(group).contains(user)){
                    adminMap.put(group, user);
                    return "SUCCESS";
                }
                else{
                    throw new RuntimeException("User is not a participant");
                }
            }
            else{
                throw new RuntimeException("Approver does not have rights");
            }
        }
        else{
            throw new RuntimeException("Group does not exist");
        }
    }

    public int removeUser(User user) {
        for (Group group : groupUserMap.keySet()){
            for (User member : groupUserMap.get(group)){
                if (member.equals(user)){
                    if (adminMap.get(group).equals(user)){
                        throw new RuntimeException("Cannot remove admin");
                    }
                    else{
                        List<Message> messages = groupMessageMap.get(group);
                        for (Message message : messages){
                            if (senderMap.get(message).equals(user)){
                                List<Message> msgs = groupMessageMap.get(group);
                                if (msgs.size() > 1){
                                    msgs.remove(message);
                                    groupMessageMap.put(group, msgs);
                                }
                                else{
                                    groupMessageMap.remove(group);
                                }
                                senderMap.remove(message);
                            }
                        }
                        List<User> users = groupUserMap.get(group);
                        if (users.size() > 1){
                            users.remove(user);
                            groupUserMap.put(group, users);
                            group.setNumberOfParticipants(users.size());
                        }
                        else{
                            groupUserMap.remove(group);
                        }
                        return group.getNumberOfParticipants() + groupMessageMap.get(group).size() + senderMap.size();
                    }
                }
            }
        }
        throw new RuntimeException("User not found");
    }

    public String findMessage(Date start, Date end, int k) {
        int count = 0;
        for (Message message : senderMap.keySet()){
            if (message.getTimestamp().compareTo(start) > 0 && message.getTimestamp().compareTo(end) < 0){
                count++;
                if (count == k){
                    return message.getContent();
                }
            }
            if (message.getTimestamp().compareTo(end) > 0){
                break;
            }
        }
        throw new RuntimeException("K is greater than the number of messages");
    }
}
