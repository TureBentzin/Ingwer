package de.bentzin.ingwer.message;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.utils.cmdreturn.CommandReturn;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.ApiStatus;
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


    public ArrayList<FramedMessage> generate(int pageLength, UUID uuid, Consumer<Integer> consumer) {
        pageLength = pageLength + 1; //user interface
        Queue<Collection<OneLinedMessage>> queue = new LinkedList<>();
        ArrayList<OneLinedMessage> arrayList = new ArrayList<>(content);
        nextPage(arrayList, pageLength, queue);
        ArrayList<FramedMessage> pages = new ArrayList<>();
        int length = queue.size();
        while (!queue.isEmpty()) {
            ArrayList<OneLinedMessage> a = new ArrayList<>(queue.remove());
            //add footer
            int size = queue.size();
            ComponentMessage componentMessage = new ComponentMessage(generatePageFooter(16, length - size, length, consumer, uuid));
            a.add(componentMessage);
            pages.add(new FramedMessage(a));
        }
        return pages;
    }

    public FramedMultipageMessageGenerator sort(Comparator<OneLinedMessage> sorter) {
        content.sort(sorter);
        return this;
    }

    @ApiStatus.Experimental
    public FramedMultipageMessageGenerator sortAlphabet() {
        content.sort((o1, o2) -> {
            String s2 = o2.getOneLinedString();
            String s1 = o1.getOneLinedString();
            if (s1.equalsIgnoreCase(s2)) {
                return 0;
            }
            return s1.toLowerCase().compareTo(s2.toLowerCase());
        });
        return this;
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
     * @param page    the page
     * @param maxPage the last page of this block
     * @return footer
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
        return " ".repeat(Math.max(0, space));
    }
}
