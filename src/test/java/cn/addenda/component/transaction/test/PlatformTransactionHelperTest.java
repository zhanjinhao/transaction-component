package cn.addenda.component.transaction.test;

import cn.addenda.component.transaction.TransactionAttrBuilder;
import cn.addenda.component.transaction.test.mybatis.TxTest;
import org.junit.Test;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.TransactionAttribute;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;

import static org.junit.Assert.*;

/**
 * @author addenda
 * @since 2022/2/27
 */
public class PlatformTransactionHelperTest extends TransactionBaseTest {

  @Test
  public void testNormalCommit() {
    try {

      Integer result = platformTransactionHelper.doTransaction(() -> {
        return txTestMapper.insert(new TxTest(1L, "testNormalCommit", null));
      });

      assertEquals(1, result.intValue());

      // 验证数据已提交
      assertNotNull(txTestMapper.selectById(1L));

    } catch (Exception e) {
      fail("事务应正常提交，但抛出异常: " + e.getMessage());
    }
  }


  @Test
  public void testRollbackOnRuntimeException() {
    try {
      platformTransactionHelper.doTransaction(RuntimeException.class, () -> {
        txTestMapper.insert(new TxTest(2L, "testRollbackOnRuntimeException", null));
        assertNotNull(txTestMapper.selectById(2L));
        throw new RuntimeException("强制回滚");
      });

      fail("预期抛出运行时异常");

    } catch (Exception e) {
      // 捕获异常后检查数据库中记录是否未增加
      assertNull(txTestMapper.selectById(2L));
    }
  }


  @Test
  public void testCustomTransactionAttribute() {
    try {
      TransactionAttribute attr = TransactionAttrBuilder.newBuilder()
              .withPropagation(Propagation.REQUIRES_NEW)
              .withIsolation(Isolation.READ_COMMITTED)
              .rollbackFor(Exception.class)
              .build();

      Integer result = platformTransactionHelper.doTransaction(attr, () -> {
        return txTestMapper.insert(new TxTest(3L, "testCustomTransactionAttribute", null));
      });

      assertEquals(1, result.intValue());

      assertNotNull(txTestMapper.selectById(3L));

    } catch (Exception e) {
      fail("自定义事务属性执行失败: " + e.getMessage());
    }
  }

  @Test
  public void testNoRollbackOnCheckedException() {
    try {
      platformTransactionHelper.doTransaction(IllegalArgumentException.class, () -> {
        txTestMapper.insert(new TxTest(4L, "testNoRollbackOnCheckedException", null));
        throw new IOException("这是一个受检异常，不应触发回滚");
      });

      fail("应该抛出受检异常并继续提交");

    } catch (UndeclaredThrowableException e) {
      // 受检异常不会导致回滚，所以数据应存在
      assertNotNull(txTestMapper.selectById(4L));
    } catch (Exception e) {
      fail("意外异常: " + e.getMessage());
    }
  }


  @Test
  public void testNestedTransactionPropagationSuccess() {
    try {
      Integer outer = platformTransactionHelper.doTransaction(() -> {
        txTestMapper.insert(new TxTest(5L, "testNestedTransactionPropagationSuccessOuter", null));

        return platformTransactionHelper.doTransaction(() -> {
          txTestMapper.insert(new TxTest(6L, "testNestedTransactionPropagationSuccessInner", null));
          return 1;
        });
      });

      assertEquals(1L, outer.intValue());

      assertNotNull(txTestMapper.selectById(5L));
      assertNotNull(txTestMapper.selectById(6L));

    } catch (Exception e) {
      fail("嵌套事务执行失败: " + e.getMessage());
    }
  }


  @Test
  public void testNestedTransactionPropagationFailed() {
    try {
      Integer outer = platformTransactionHelper.doTransaction(() -> {
        txTestMapper.insert(new TxTest(7L, "testNestedTransactionPropagationFailedOuter", null));

        Integer inter = platformTransactionHelper.doTransaction(() -> {
          txTestMapper.insert(new TxTest(8L, "testNestedTransactionPropagationFailedInner", null));
          return 1;
        });

        throw new NullPointerException();
      });

      assertEquals(1L, outer.intValue());

    } catch (NullPointerException nullPointerException) {
      assertNull(txTestMapper.selectById(7L));
      assertNull(txTestMapper.selectById(8L));
    } catch (Exception e) {
      fail("嵌套事务执行失败: " + e.getMessage());
    }
  }


  @Test
  public void testNestedTransactionWithRequiresNew() {
    try {
      // 外层事务
      Integer result = platformTransactionHelper.doTransaction(() -> {
        txTestMapper.insert(new TxTest(10L, "outer-before", null));

        // 内层事务：REQUIRES_NEW 独立事务
        TransactionAttribute attr = TransactionAttrBuilder.newBuilder()
                .withPropagation(Propagation.REQUIRES_NEW)
                .rollbackFor(Exception.class)
                .build();

        try {
          platformTransactionHelper.doTransaction(attr, () -> {
            txTestMapper.insert(new TxTest(11L, "inner", null));
            throw new RuntimeException("Inner transaction rollback");
          });
        } catch (Exception e) {
          // 捕获内层事务异常，外层继续执行
          System.out.println("Inner transaction rolled back: " + e.getMessage());
        }

        txTestMapper.insert(new TxTest(12L, "outer-after", null));
        return 1;
      });

      assertEquals(1, result.intValue());

      // 验证外层事务提交成功
      assertNotNull(txTestMapper.selectById(10L));
      assertNotNull(txTestMapper.selectById(12L));

      // 验证内层事务回滚（ID=11 不应存在）
      assertNull(txTestMapper.selectById(11L));
    } catch (Exception e) {
      fail("外层事务不应失败: " + e.getMessage());
    }
  }

}
