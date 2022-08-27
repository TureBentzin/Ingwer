package de.bentzin.ingwer.storage;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.thow.IngwerException;
import de.bentzin.ingwer.thow.IngwerThrower;
import org.scijava.util.ClassUtils;
import org.scijava.util.FileUtils;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class Sqlite {


    public static void init() throws URISyntaxException {
        URL file = ClassUtils.getLocation(Ingwer.class);
        File data = new File(file.toURI());
        System.out.println(data);
    }

    public static void main(String[] args) {
        try {
            init();
        } catch (URISyntaxException e) {
            IngwerThrower.accept(new IngwerException(e));
        }
    }
}
