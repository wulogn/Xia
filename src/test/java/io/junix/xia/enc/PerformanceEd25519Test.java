package io.junix.xia.enc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import static org.junit.jupiter.api.Assertions.*;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.InvalidKeyException;

/**
 * Ed25519 性能测试类
 * 
 * 注意：这些测试可能需要较长时间运行，因此默认被禁用。
 * 要运行这些测试，请移除@Test注解上的@Disabled注解。
 */
public class PerformanceEd25519Test {

    // 测试运行时间阈值（毫秒）
    private static final long TIME_THRESHOLD = 5000; // 5秒
    
    @Test
    @Disabled("性能测试耗时较长，默认禁用")
    public void testKeyGenerationPerformance() {
        int iterations = 1000;
        long startTime = System.currentTimeMillis();
        
        // 批量生成密钥对
        for (int i = 0; i < iterations; i++) {
            KeyPair keyPair = Ed25519.generateKeyPair();
            assertNotNull(keyPair);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("生成 " + iterations + " 个密钥对耗时: " + duration + " 毫秒");
        System.out.println("平均每个密钥对生成时间: " + (double) duration / iterations + " 毫秒");
        
        // 确保性能在合理范围内
        assertTrue(duration < TIME_THRESHOLD, "密钥对生成性能低于预期");
    }
    
    @Test
    @Disabled("性能测试耗时较长，默认禁用")
    public void testSigningPerformance() throws InvalidKeyException, SignatureException {
        int iterations = 1000;
        KeyPair keyPair = Ed25519.generateKeyPair();
        byte[] message = "Performance test message".getBytes();
        
        long startTime = System.currentTimeMillis();
        
        // 批量签名
        for (int i = 0; i < iterations; i++) {
            byte[] signature = Ed25519.sign(message, keyPair.getPrivate());
            assertNotNull(signature);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("执行 " + iterations + " 次签名操作耗时: " + duration + " 毫秒");
        System.out.println("平均每次签名操作时间: " + (double) duration / iterations + " 毫秒");
        
        // 确保性能在合理范围内
        assertTrue(duration < TIME_THRESHOLD, "签名操作性能低于预期");
    }
    
    @Test
    @Disabled("性能测试耗时较长，默认禁用")
    public void testVerificationPerformance() throws InvalidKeyException, SignatureException {
        int iterations = 1000;
        KeyPair keyPair = Ed25519.generateKeyPair();
        byte[] message = "Performance test message".getBytes();
        byte[] signature = Ed25519.sign(message, keyPair.getPrivate());
        
        long startTime = System.currentTimeMillis();
        
        // 批量验证
        for (int i = 0; i < iterations; i++) {
            boolean isValid = Ed25519.verify(message, keyPair.getPublic(), signature);
            assertTrue(isValid);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("执行 " + iterations + " 次验证操作耗时: " + duration + " 毫秒");
        System.out.println("平均每次验证操作时间: " + (double) duration / iterations + " 毫秒");
        
        // 确保性能在合理范围内
        assertTrue(duration < TIME_THRESHOLD, "验证操作性能低于预期");
    }
    
    @Test
    @Disabled("性能测试耗时较长，默认禁用")
    public void testEndToEndPerformance() throws InvalidKeyException, SignatureException {
        int iterations = 1000;
        byte[] message = "End to end performance test message".getBytes();
        
        long startTime = System.currentTimeMillis();
        
        // 完整的端到端测试：生成密钥 -> 签名 -> 验证
        for (int i = 0; i < iterations; i++) {
            KeyPair keyPair = Ed25519.generateKeyPair();
            byte[] signature = Ed25519.sign(message, keyPair.getPrivate());
            boolean isValid = Ed25519.verify(message, keyPair.getPublic(), signature);
            
            assertNotNull(keyPair);
            assertNotNull(signature);
            assertTrue(isValid);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("执行 " + iterations + " 次端到端操作耗时: " + duration + " 毫秒");
        System.out.println("平均每次端到端操作时间: " + (double) duration / iterations + " 毫秒");
        
        // 确保性能在合理范围内
        assertTrue(duration < TIME_THRESHOLD * 2, "端到端操作性能低于预期");
    }
    
    @Test
    @Disabled("性能测试耗时较长，默认禁用")
    public void testLargeMessagePerformance() throws InvalidKeyException, SignatureException {
        // 测试大消息的签名和验证性能
        StringBuilder largeMessageBuilder = new StringBuilder();
        for (int i = 0; i < 100000; i++) { // 100KB消息
            largeMessageBuilder.append("A");
        }
        byte[] largeMessage = largeMessageBuilder.toString().getBytes();
        
        KeyPair keyPair = Ed25519.generateKeyPair();
        
        long startTime = System.currentTimeMillis();
        
        byte[] signature = Ed25519.sign(largeMessage, keyPair.getPrivate());
        boolean isValid = Ed25519.verify(largeMessage, keyPair.getPublic(), signature);
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("100KB消息签名和验证耗时: " + duration + " 毫秒");
        
        assertNotNull(signature);
        assertTrue(isValid);
        assertTrue(duration < TIME_THRESHOLD, "大消息处理性能低于预期");
    }
    
    @Test
    @Disabled("压力测试耗时较长，默认禁用")
    public void stressTest() throws InvalidKeyException, SignatureException {
        int iterations = 10000;
        byte[] message = "Stress test message".getBytes();
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < iterations; i++) {
            KeyPair keyPair = Ed25519.generateKeyPair();
            byte[] signature = Ed25519.sign(message, keyPair.getPrivate());
            boolean isValid = Ed25519.verify(message, keyPair.getPublic(), signature);
            
            assertNotNull(keyPair);
            assertNotNull(signature);
            assertTrue(isValid);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("压力测试 " + iterations + " 次迭代耗时: " + duration + " 毫秒");
        System.out.println("平均每秒处理: " + (iterations * 1000.0 / duration) + " 次迭代");
    }
}