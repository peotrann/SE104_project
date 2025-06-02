package com.example.SE104_DoAn;

import java.util.ArrayList;
import java.util.List;

public class TaskList {
    private String title;
    private List<Card> cards;

    public TaskList(String title) {
        this.title = title;
        this.cards = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public void removeCard(int position) {
        cards.remove(position);
    }
}