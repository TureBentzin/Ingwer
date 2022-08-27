package de.bentzin.ingwer.identity.permissions;

import de.bentzin.ingwer.thow.IngwerException;
import de.bentzin.ingwer.thow.IngwerThrower;
import org.jetbrains.annotations.NotNull;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;

public enum IngwerPermission {

    USE,
    SUPERADMIN,
    FEATURE_WELCOME,
    

    ;

    public static long generatePermissions(IngwerPermissions ingwerPermissions){
        IngwerPermission[] permissions = values();
        StringBuilder bin = new StringBuilder();
        for (IngwerPermission permission : permissions) {
            bin.append(ingwerPermissions.contains(permission) ? '1' : '0');
        }
        return Long.parseLong(bin.toString(),2);
    }

    public static @NotNull IngwerPermissions decodePermissions(long permissions) {
        Long l = permissions;
        IngwerPermission[] values = values();
        IngwerPermissions ingwerPermissions = new IngwerPermissions();
        char[] chars = Long.toBinaryString(l).toCharArray();
        if(chars.length != values.length) {
            //IngwerThrower.accept(new InvalidParameterException("permissions needs to have a length of " + values.length + " -> " + String.copyValueOf(chars)));
            int i = values.length - chars.length;
            char[] chars1 = new char[values.length];
            if(chars.length > 0)
                System.arraycopy(chars, 0, chars1, 0, chars.length);

            for (char c : chars1) {
                if( c == 0) c = '0';
            }
            chars = chars1;
        }

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if(c != '1' && c != '0') {
                IngwerThrower.acceptS(new InvalidParameterException("permissions needs to be a binary chain!"));
            }
            if(c == '1')
                ingwerPermissions.add(values[i]);
        }


        return ingwerPermissions;
    }





}
