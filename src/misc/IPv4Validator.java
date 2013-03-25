/*
 * Juan Antonio Aldea Armenteros (0404450)
 * LUT - Object Oriented Programming Techniques
 *                  2013
 */

package misc;

import java.util.regex.Pattern;

public class IPv4Validator {
    protected static final String IPV4_REGEXP = "(0?\\d?\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\."
                                                      + "(0?\\d?\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\."
                                                      + "(0?\\d?\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\."
                                                      + "(0?\\d?\\d|1\\d\\d|2[0-4]\\d|25[0-5])$";

    public static boolean isValidIPv4(String address) {
        return Pattern.matches(IPV4_REGEXP, address);
    }
}
