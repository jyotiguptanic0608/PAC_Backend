package com.proposal.service;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class SessionRegistryService {

    private final ConcurrentHashMap<String,String>
            activeSessions=new ConcurrentHashMap<>();


    public boolean hasSession(String username){

        return activeSessions.containsKey(username);

    }

    public void addSession(String username,
                           String sessionId){

        activeSessions.put(username,sessionId);

    }

    public void removeSession(String username){

        activeSessions.remove(username);

    }

    public String getSessionId(String username){

        return activeSessions.get(username);

    }

}