package de.bentzin.ingwer.message;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.utils.cmdreturn.CommandReturn;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.Contract;
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


    public void doRoutine(int pageLength, UUID uuid) {

    }


    public ArrayList<FramedMessage> generate(int pageLength, UUID uuid, Consumer<Integer> consumer) {
        pageLength = pageLength + 1; //user interface
        Queue<Collection<OneLinedMessage>> queue = new LinkedList<>();
        ArrayList<OneLinedMessage> arrayList = new ArrayList<>(content);
        nextPage(arrayList, pageLength, queue);
        ArrayList<FramedMessage> pages = new ArrayList<>();
        int length = queue.size();
        while (!queue.isEmpty()) {
            //TODO: add footer
            ArrayList<OneLinedMessage> a = new ArrayList<>(queue.remove());
            //add footer
            int size = queue.size();
            ComponentMessage componentMessage = new ComponentMessage(generatePageFooter(16, length - size, length, consumer, uuid));
            a.add(componentMessage);
            pages.add(new FramedMessage(a));

        }
        return pages;
    }

    private void nextPage(@NotNull List<OneLinedMessage> remaining, int pageLength, Queue<Collection<OneLinedMessage>> queue) {
        if (remaining.size() <= pageLength) {
            queue.add(remaining);
        } else {
            List<OneLinedMessage> oneLinedMessages = remaining.subList(0, pageLength - 1).stream().toList();
            remaining.removeAll(oneLinedMessages);
            queue.add(oneLinedMessages);
            nextPage(remaining, pageLength, queue);
        }
    }

    /**
     * @param page
     * @param maxPage
     * @return
     */
    public Component generatePageFooter(int footer_space, int page, int maxPage, Consumer<Integer> pageSelect, UUID uuid) {
        //<gray>               [<-] <blue>Page 2 of 4 <gray>[->]
        if (page == maxPage && page == 1) return IngwerMessage.mm(delay(footer_space) + pages(page, maxPage));
        if (page > maxPage || page <= 0) {
            throw new InvalidParameterException("page and maxPage need to be valid!");
        }
        CommandReturn increase_command = Ingwer.getCommandReturnSystem().addNewReturn(() -> pageSelect.accept(page + 1), uuid);
        CommandReturn decrease_command = Ingwer.getCommandReturnSystem().addNewReturn(() -> pageSelect.accept(page - 1), uuid);

        String increase = "<gray><click:run_command:'" + increase_command.command() + "'>[->]</click>";
        String decrease = "<gray><click:run_command:'" + decrease_command.command() + "'>[<-]</click>";

        PlainTextComponentSerializer text = PlainTextComponentSerializer.plainText();
        return IngwerMessage.mm(
                delay(footer_space) +
                        (page == 1 ? delay(text.serialize(IngwerMessage.mm(decrease)).length()) : decrease) +
                        pages(page, maxPage) +
                        (page == maxPage ? delay(text.serialize(IngwerMessage.mm(increase)).length()) : increase)
        );
    }

    @Contract(pure = true)
    private @NotNull String pages(int page, int maxPage) {
        if (page == maxPage && page == 1) return "<aqua>   Page 1 of 1</aqua>";
        return "<aqua> Page " + page + " of " + maxPage + " </aqua>";
    }

    private @NotNull String delay(int space) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < space; i++) {
            builder.append(" ");
        }
        return builder.toString();
    }
}
