package com.example.SE104_DoAn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskList {
    private List<Card> cards = new ArrayList<>();
    private String creator;
    private Map<String, String> members = new HashMap<>(); // key: email, value: role (leader/member)

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = new ArrayList<>(cards);
    }

    public void addCard(Card card) {
        this.cards.add(card);
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Map<String, String> getMembers() {
        return members;
    }

    public void setMembers(Map<String, String> members) {
        this.members = new HashMap<>(members);
    }

    public void addMember(String email, String role) {
        members.put(email, role);
    }
}