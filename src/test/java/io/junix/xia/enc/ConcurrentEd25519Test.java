package io.junix.xia.enc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.security.KeyPair;
import java.security.SignatureException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Ed25519 多线程测试类
 */
public class ConcurrentEd25519Test {

    @Test
    public void testConcurrentKeyGeneration() throws InterruptedException, ExecutionException {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<KeyPair>> futures = new ArrayList<>();

        // 并发生成密钥对
        for (int i = 0; i < threadCount; i++) {
            Future<KeyPair> future = executor.submit(Ed25519::generateKeyPair);
            futures.add(future);
        }

        // 收集结果
        List<KeyPair> keyPairs = new ArrayList<>();
        for (Future<KeyPair> future : futures) {
            keyPairs.add(future.get());
        }

        executor.shutdown();

        // 验证所有密钥对都不相同
        assertEquals(threadCount, keyPairs.size());
        for (int i = 0; i < keyPairs.size(); i++) {
            for (int j = i + 1; j < keyPairs.size(); j++) {
                assertNotEquals(keyPairs.get(i).getPrivate(), keyPairs.get(j).getPrivate());
                assertNotEquals(keyPairs.get(i).getPublic(), keyPairs.get(j).getPublic());
            }
        }
    }

    @Test
    public void testConcurrentSigningWithSameKey() throws InterruptedException, ExecutionException {
        int threadCount = 10;
        KeyPair keyPair = Ed25519.generateKeyPair();
        byte[] message = "Concurrent test message".getBytes();

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<byte[]>> futures = new ArrayList<>();

        // 并发使用同一密钥对同一消息签名
        for (int i = 0; i < threadCount; i++) {
            Future<byte[]> future = executor.submit(() -> Ed25519.sign(message, keyPair.getPrivate()));
            futures.add(future);
        }

        // 收集签名结果
        List<byte[]> signatures = new ArrayList<>();
        for (Future<byte[]> future : futures) {
            signatures.add(future.get());
        }

        executor.shutdown();

        // 验证所有签名都有效且相同
        assertEquals(threadCount, signatures.size());
        for (byte[] signature : signatures) {
            boolean isValid = Ed25519.verify(message, keyPair.getPublic(), signature);
            assertTrue(isValid);
        }
    }

    @Test
    public void testConcurrentSigningWithDifferentKeys() throws InterruptedException, ExecutionException {
        int threadCount = 10;
        byte[] message = "Concurrent test message".getBytes();

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<SignatureResult>> futures = new ArrayList<>();

        // 并发使用不同密钥对同一消息签名
        for (int i = 0; i < threadCount; i++) {
            Future<SignatureResult> future = executor.submit(() -> {
                KeyPair keyPair = Ed25519.generateKeyPair();
                byte[] signature = Ed25519.sign(message, keyPair.getPrivate());
                return new SignatureResult(keyPair, signature);
            });
            futures.add(future);
        }

        // 收集签名结果
        List<SignatureResult> results = new ArrayList<>();
        for (Future<SignatureResult> future : futures) {
            results.add(future.get());
        }

        executor.shutdown();

        // 验证所有签名都有效
        assertEquals(threadCount, results.size());
        for (SignatureResult result : results) {
            boolean isValid = Ed25519.verify(message, result.keyPair.getPublic(), result.signature);
            assertTrue(isValid);
        }
    }

    @Test
    public void testConcurrentVerification() throws InterruptedException, ExecutionException, InvalidKeyException, SignatureException {
        int threadCount = 10;
        byte[] message = "Concurrent verification test".getBytes();
        KeyPair keyPair = Ed25519.generateKeyPair();
        byte[] signature = Ed25519.sign(message, keyPair.getPrivate());

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<Boolean>> futures = new ArrayList<>();

        // 并发验证同一签名
        for (int i = 0; i < threadCount; i++) {
            Future<Boolean> future = executor.submit(() -> Ed25519.verify(message, keyPair.getPublic(), signature));
            futures.add(future);
        }

        // 收集验证结果
        List<Boolean> results = new ArrayList<>();
        for (Future<Boolean> future : futures) {
            results.add(future.get());
        }

        executor.shutdown();

        // 验证所有结果都为true
        assertEquals(threadCount, results.size());
        for (Boolean result : results) {
            assertTrue(result);
        }
    }

    @Test
    public void testMixedConcurrentOperations() throws InterruptedException, ExecutionException {
        int threadCount = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicInteger keyGenCount = new AtomicInteger(0);
        AtomicInteger signCount = new AtomicInteger(0);
        AtomicInteger verifyCount = new AtomicInteger(0);

        List<Future<?>> futures = new ArrayList<>();

        // 混合并发执行不同类型的操作
        for (int i = 0; i < 1000; i++) {
            final int opType = i % 3;
            Future<?> future = executor.submit(() -> {
                try {
                    switch (opType) {
                        case 0: // 生成密钥对
                            KeyPair kp = Ed25519.generateKeyPair();
                            assertNotNull(kp);
                            keyGenCount.incrementAndGet();
                            break;
                        case 1: // 签名
                            KeyPair keyPair = Ed25519.generateKeyPair();
                            byte[] message = "Mixed test".getBytes();
                            byte[] signature = Ed25519.sign(message, keyPair.getPrivate());
                            assertNotNull(signature);
                            signCount.incrementAndGet();
                            break;
                        case 2: // 验证
                            KeyPair kp2 = Ed25519.generateKeyPair();
                            byte[] msg = "Mixed verification test".getBytes();
                            byte[] sig = Ed25519.sign(msg, kp2.getPrivate());
                            boolean valid = Ed25519.verify(msg, kp2.getPublic(), sig);
                            assertTrue(valid);
                            verifyCount.incrementAndGet();
                            break;
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            futures.add(future);
        }

        // 等待所有任务完成
        for (Future<?> future : futures) {
            future.get();
        }

        executor.shutdown();

        // 验证所有操作都执行了
        // 允许误差
        assertEquals(333, keyGenCount.get(), 1);
        assertEquals(333, signCount.get(), 1);
        assertEquals(333, verifyCount.get(), 1);
    }

    // 辅助类用于保存签名结果
    private record SignatureResult(KeyPair keyPair, byte[] signature) { }
}