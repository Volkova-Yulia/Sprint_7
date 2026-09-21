package scooter.util;

import scooter.model.Courier;

import java.util.concurrent.ThreadLocalRandom;

public class CourierGenerator {

    public static Courier getRandom() {
        String suffix = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 999999));
        return new Courier("courier_" + suffix, "pass_" + suffix, "name_" + suffix);
    }
}
