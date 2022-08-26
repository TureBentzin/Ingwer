package de.bentzin.ingwer.identity.permissions;

import java.util.Arrays;

public class Test {



        public static void main(String[] args) {
            IngwerPermissions ingwerPermissions

                    = new IngwerPermissions(IngwerPermission.USE);


            System.out.println(Arrays.toString(IngwerPermission.values()));

            System.out.println("ingwerPermissions = " + ingwerPermissions);
            long l = IngwerPermission.generatePermissions(ingwerPermissions);
            System.out.println("l = " + l);
            IngwerPermissions decodePermissions = IngwerPermission.decodePermissions(l);
            System.out.println("decodePermissions = " + decodePermissions);
        }

}
