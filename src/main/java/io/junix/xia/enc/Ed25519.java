package io.junix.xia.enc;

import java.security.*;

/**
 * Ed25519非对称加密算法工具类
 *
 * @author wulogn
 */
public class Ed25519 {

    private static KeyPairGenerator kpg;

    static {
        try {
            kpg = KeyPairGenerator.getInstance("Ed25519");
            // 仅验证是否存在该算法
            Signature.getInstance("Ed25519");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Ed25519 algorithm not found!");
            System.exit(1);
        }
    }

    public static KeyPair generateKeyPair() {
        return kpg.generateKeyPair();
    }

    public static byte[] sign(byte[] message, PrivateKey sk) throws InvalidKeyException, SignatureException {
        try {
            Signature sig = Signature.getInstance("Ed25519");
            sig.initSign(sk);
            sig.update(message);
            return sig.sign();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean verify(byte[] message, PublicKey pk, byte[] signature) {
        try {
            Signature sig = Signature.getInstance("Ed25519");
            sig.initVerify(pk);
            sig.update(message);
            return sig.verify(signature);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            System.err.println("Invalid public key: " + new String(pk.getEncoded()));
            return false;
        } catch (SignatureException e) {
            e.printStackTrace(System.err);
            return false;
        }
    }

}
