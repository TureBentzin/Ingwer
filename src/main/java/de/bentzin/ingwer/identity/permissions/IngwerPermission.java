package de.bentzin.ingwer.identity.permissions;

import de.bentzin.ingwer.thow.IngwerThrower;
import org.jetbrains.annotations.NotNull;

import java.security.InvalidParameterException;

public enum IngwerPermission {

    USE,
    SUPERADMIN,
    ADMIN,
    SYSTEM_CTRL,
    TRUST,


    ;

    public static long generatePermissions(IngwerPermissions ingwerPermissions) {
        IngwerPermission[] permissions = values();
        StringBuilder bin = new StringBuilder();
        for (IngwerPermission permission : permissions) {
            bin.append(ingwerPermissions.contains(permission) ? '1' : '0');
        }
        return Long.parseLong(bin.toString(), 2);
    }

    public static @NotNull IngwerPermissions decodePermissions(long permissions) {
        Long l = permissions;
        IngwerPermission[] values = values();
        IngwerPermissions ingwerPermissions = new IngwerPermissions();
        char[] chars = Long.toBinaryString(l).toCharArray();
        if (chars.length != values.length) {
            //IngwerThrower.accept(new InvalidParameterException("permissions needs to have a length of " + values.length + " -> " + String.copyValueOf(chars)));
            int i = values.length - chars.length;
            char[] chars1 = new char[values.length];
            if (chars.length > 0)
                System.arraycopy(chars, 0, chars1, 0, chars.length);

            for (int count = 0; count < chars1.length; count++) {
                if (chars1[count] == 0) chars1[count] = '0';
            }
            chars = chars1;
        }

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c != '1' && c != '0') {
                try {
                    throw new InvalidParameterException("permissions needs to be a binary chain! >> " + c);
                } catch (InvalidParameterException parameterException) {
                    IngwerThrower.acceptS(parameterException);
                }
            }
            if (c == '1')
                ingwerPermissions.add(values[i]);
        }


        return ingwerPermissions;
    }


}
