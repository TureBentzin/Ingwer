package de.bentzin.ingwer.message;

import de.bentzin.ingwer.tests.TempReturnCommandSystem;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.function.Consumer;

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
            //TODO: add footer
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

    /**
     *
     * @param page
     * @param maxPage
     * @return
     */
    public Component generatePageFooter(int footer_space,int page, int maxPage, Consumer<Integer> pageSelect) {
        //<gray>               [<-] <blue>Page 2 of 4 <gray>[->]
        if(page == maxPage) return IngwerMessage.mm(delay(footer_space) + "<blue>Page 1");
        if(page > maxPage || page <= 0) {
            throw new InvalidParameterException("page and maxPage need to be valid!");
        }
        String increase_command = TempReturnCommandSystem.addReturn(() -> pageSelect.accept(page + 1));
        String decrease_command = TempReturnCommandSystem.addReturn(() -> pageSelect.accept(page - 1));

        String increase = "<gray><click:run_command:'" + increase_command + "'>[->]</click>";
        String decrease = "<gray><click:run_command:'" + decrease_command + "'>[<-]</click>";
        //TODO WORK
        return null;
    }

    private @NotNull String delay(int space) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < space; i++) {
            builder.append(" ");
        }
        return builder.toString();
    }
}
