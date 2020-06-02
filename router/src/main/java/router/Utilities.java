package router;

// Imports

// Class Decleration
    /*
        Provides log methods
        Provides FIX message creation with checksum
    */
class Utilities {
    // Logging
        static void print(String message) {
            System.out.print(message);
        }
        static void println(String message) {
            System.out.println(message);
        }
    // FIX
        static Integer calculateChecksum(String message){
            byte[] bytes = message.getBytes();
            Integer checksum = 0;
            for (byte b : bytes) {
                checksum += b;
            }
            return checksum % 256;
        }

}