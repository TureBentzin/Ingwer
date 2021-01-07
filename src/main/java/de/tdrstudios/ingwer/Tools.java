package de.tdrstudios.ingwer;

public class Tools {
    protected static  Chat chat = new Chat();
    public static Chat getChat() {
        return chat;
    }


}
class Chat {

    private String prefix;

    public void setPrefix(char prefix) {
        this.prefix = prefix + "Ingwer";
    }
    public String getPrefix() {
        return prefix;
    }
}

