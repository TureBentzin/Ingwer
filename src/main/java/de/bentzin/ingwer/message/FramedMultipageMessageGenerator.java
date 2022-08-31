package de.bentzin.ingwer.message;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class FramedMultipageMessageGenerator {

    private final List<OneLinedMessage> content;

    public FramedMultipageMessageGenerator(List<OneLinedMessage> content) {
        this.content = content;
    }

    public List<OneLinedMessage> getContent() {
        return content;
    }

    public Iterable<FramedMessage> generate(int pageLength) {
        Queue<Collection<OneLinedMessage>> queue = new PriorityQueue<>();
        ArrayList<OneLinedMessage> arrayList = new ArrayList<>(content);
        nextPage(arrayList,pageLength,queue);
        ArrayList<FramedMessage> pages = new ArrayList<>();
        while (!queue.isEmpty()){
            pages.add(new FramedMessage(queue.remove()));
        }
        return pages;
    }

    private void nextPage(@NotNull List<OneLinedMessage> remaining, int pageLength, Queue<Collection<OneLinedMessage>> queue) {
        if(remaining.size() <= pageLength) {
            queue.add(remaining);
        }else {
            List<OneLinedMessage> oneLinedMessages = remaining.subList(0, pageLength - 1);
            remaining.removeAll(oneLinedMessages);
            queue.add(oneLinedMessages);
            nextPage(remaining,pageLength,queue);
        }
    }
}
