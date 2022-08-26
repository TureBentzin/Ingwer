package de.bentzin.ingwer.identity.permissions;

import de.bentzin.ingwer.thow.IngwerException;
import de.bentzin.ingwer.thow.IngwerThrower;
import org.jetbrains.annotations.NotNull;

import java.security.InvalidParameterException;

public enum IngwerPermission {

    USE,


    ;

    public static long generatePermissions(IngwerPermissions ingwerPermissions){
        IngwerPermission[] permissions = values();
        StringBuilder bin = new StringBuilder();
        for (IngwerPermission permission : permissions) {
            bin.append(ingwerPermissions.contains(permission) ? '1' : '0');
        }
        return Long.getLong(bin.toString());
    }

    public static @NotNull IngwerPermissions decodePermissions(long permissions) {
        Long l = permissions;
        IngwerPermission[] values = values();
        IngwerPermissions ingwerPermissions = new IngwerPermissions();
        char[] chars = l.toString().toCharArray();
        if(chars.length != values.length) {
            IngwerThrower.accept(new InvalidParameterException("permissions needs to have a length of " + values.length));
        }

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if(c != 1 && c != 0) {
                IngwerThrower.accept(new InvalidParameterException("permissions needs to be a binary chain!"));
            }
            if(c == '1')
                ingwerPermissions.add(values[c]);
        }
        return ingwerPermissions;
    }

}
