package com.proposal.service;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;

@Service
public class SessionRegistryService {

    private final ConcurrentHashMap<String, HttpSession> activeSessions =
            new ConcurrentHashMap<>();

    public HttpSession getSession(String username) {
        return activeSessions.get(username);
    }

    public void addSession(String username,
                           HttpSession session) {
        activeSessions.put(username, session);
    }

    public void removeSession(String username) {
        activeSessions.remove(username);
    }

    public boolean hasSession(String username) {
        return activeSessions.containsKey(username);
    }
}