package com.company;

import static com.company.AESDemo.extractIV;

/**
 * Disclaimer:
 * This code is for illustration purposes.
 * Do not use in real-world deployments.
 */

public class PaddingOracleAttackSimulation {

    private static class Sender {
        private byte[] secretKey;
        private String secretMessage = "Top secret!";

        public Sender(byte[] secretKey) {
            this.secretKey = secretKey;
        }

        // This will return both iv and ciphertext
        public byte[] encrypt() {
            return AESDemo.encrypt(secretKey, secretMessage);
        }
    }

    private static class Receiver {
        private byte[] secretKey;

        public Receiver(byte[] secretKey) {
            this.secretKey = secretKey;
        }

        // Padding Oracle (Notice the return type)
        public boolean isDecryptionSuccessful(byte[] ciphertext) {
            return AESDemo.decrypt(secretKey, ciphertext) != null;
        }
    }

    public static class Adversary {

        // This is where you are going to develop the attack
        // Assume you cannot access the key.
        // You shall not add any methods to the Receiver class.
        // You only have access to the receiver's "isDecryptionSuccessful" only.
        public String extractSecretMessage(Receiver receiver, byte[] ciphertext) {

            byte[] iv = extractIV(ciphertext);


            System.out.println(AESDemo.toHex(AESDemo.extractCiphertextBlocks(ciphertext)));
            System.out.println( AESDemo.toHex(extractIV(ciphertext)));
            System.out.println((byte)(0xff & ((int)0x24^(int)ciphertext[10])));
            iv[0]=0x08;
            iv[1]=0x06;
            iv[2]=0x05;
            iv[3]=0x32;
            iv[4]=0x54;
            iv[5]=0x34;
            iv[6]=0x65;
            iv[7]=0x33;
            iv[8]=0x67;
            iv[9]=0x55;
            iv[10]=0x22;
            iv[11]=0x65;
            System.out.println(iv.length);

            byte[] ciphertextBlocks = AESDemo.extractCiphertextBlocks(ciphertext);
            boolean result = receiver.isDecryptionSuccessful(AESDemo.prepareCiphertext(iv, ciphertextBlocks));
            System.out.println(result); // This is true initially, as the ciphertext was not altered in any way.

            // TODO: WRITE THE ATTACK HERE.


            return null;
        }
    }

    public static void main(String[] args) {

        byte[] secretKey = AESDemo.keyGen();
        Sender sender = new Sender(secretKey);
        Receiver receiver = new Receiver(secretKey);

        // The adversary does not have the key
        Adversary adversary = new Adversary();

        // Now, let's get some valid encryption from the sender
        byte[] ciphertext = sender.encrypt();

        // The adversary  got the encrypted message from the network.
        // The adversary's goal is to extract the message without knowing the key.
        String message = adversary.extractSecretMessage(receiver, ciphertext);

        System.out.println("Extracted message = " + message);
    }
}