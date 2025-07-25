package io.junix.xia.enc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.InvalidKeyException;

public class Ed25519Test {

    private KeyPair keyPair;
    private byte[] message;

    @BeforeEach
    public void setUp() {
        keyPair = Ed25519.generateKeyPair();
        message = "Hello, World!".getBytes();
    }

    @Test
    public void testGenerateKeyPair() {
        KeyPair kp1 = Ed25519.generateKeyPair();
        KeyPair kp2 = Ed25519.generateKeyPair();
        
        // 验证生成的密钥对不为空
        assertNotNull(kp1);
        assertNotNull(kp2);
        
        // 验证每次生成的密钥对是唯一的
        assertNotEquals(kp1.getPrivate(), kp2.getPrivate());
        assertNotEquals(kp1.getPublic(), kp2.getPublic());
    }

    @Test
    public void testSign() throws InvalidKeyException, SignatureException {
        byte[] signature = Ed25519.sign(message, keyPair.getPrivate());
        
        // 验证签名不为空
        assertNotNull(signature);
        assertTrue(signature.length > 0);
    }

    @Test
    public void testVerify() throws InvalidKeyException, SignatureException {
        byte[] signature = Ed25519.sign(message, keyPair.getPrivate());
        boolean isValid = Ed25519.verify(message, keyPair.getPublic(), signature);
        
        // 验证签名验证通过
        assertTrue(isValid);
    }

    @Test
    public void testSignAndVerifyWithDifferentMessage() throws InvalidKeyException, SignatureException {
        byte[] signature = Ed25519.sign(message, keyPair.getPrivate());
        byte[] differentMessage = "Different message".getBytes();
        boolean isValid = Ed25519.verify(differentMessage, keyPair.getPublic(), signature);
        
        // 验证使用不同消息验证签名失败
        assertFalse(isValid);
    }

    @Test
    public void testVerifyWithTamperedSignature() throws InvalidKeyException, SignatureException {
        byte[] signature = Ed25519.sign(message, keyPair.getPrivate());
        
        // 修改签名
        signature[0] = (byte) (signature[0] + 1);
        boolean isValid = Ed25519.verify(message, keyPair.getPublic(), signature);
        
        // 验证被篡改的签名验证失败
        assertFalse(isValid);
    }

    @Test
    public void testSignWithNullPrivateKey() {
        assertThrows(InvalidKeyException.class, () -> {
            Ed25519.sign(message, null);
        });
    }

    @Test
    public void testSignWithNullMessage() throws InvalidKeyException, SignatureException {
        assertThrows(Exception.class, () -> {
            Ed25519.sign(null, keyPair.getPrivate());
        });
    }

    @Test
    public void testVerifyWithNullPublicKey() throws InvalidKeyException, SignatureException {
        byte[] signature = Ed25519.sign(message, keyPair.getPrivate());
        boolean isValid = Ed25519.verify(message, null, signature);
        
        // 验证使用空公钥验证失败
        assertFalse(isValid);
    }

    @Test
    public void testVerifyWithNullSignature() {
        boolean isValid = Ed25519.verify(message, keyPair.getPublic(), null);
        
        // 验证使用空签名验证失败
        assertFalse(isValid);
    }

    @Test
    public void testVerifyWithNullMessage() throws InvalidKeyException, SignatureException {
        byte[] signature = Ed25519.sign(message, keyPair.getPrivate());
        boolean isValid = Ed25519.verify(null, keyPair.getPublic(), signature);
        
        // 验证使用空消息验证失败
        assertFalse(isValid);
    }

    @Test
    public void testEmptyMessageSignAndVerify() throws InvalidKeyException, SignatureException {
        byte[] emptyMessage = new byte[0];
        byte[] signature = Ed25519.sign(emptyMessage, keyPair.getPrivate());
        boolean isValid = Ed25519.verify(emptyMessage, keyPair.getPublic(), signature);
        
        // 验证空消息的签名和验证
        assertTrue(isValid);
    }
}