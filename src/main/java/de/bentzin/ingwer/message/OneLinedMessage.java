package de.bentzin.ingwer.message;

import net.kyori.adventure.text.Component;

public interface OneLinedMessage extends IngwerMessage {
    String getOneLinedString();

    Component getOneLinedComponent();
}
