package cn.addenda.component.transaction.test;

import cn.addenda.component.transaction.test.mybatis.TxTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author addenda
 * @since 2022/2/27
 */
public class TransactionTemplateTest extends TransactionBaseTest {

  @Test
  public void test() {

    Integer integer = transactionTemplate.execute(status -> {
      try {
        return txTestMapper.insert(new TxTest(1L, "VoidTxExecutor", "123"));
      } catch (Exception e) {
        status.setRollbackOnly();
        throw e;
      }
    });
    Assert.assertNotNull(txTestMapper.selectById(1L));

    try {
      transactionTemplate.executeWithoutResult(status -> {
        try {
          txTestMapper.insert(new TxTest(2L, "VoidTxExecutor", "123"));
          throw new IllegalArgumentException();
        } catch (Exception e) {
          status.setRollbackOnly();
          throw e;
        }
      });
    } catch (IllegalArgumentException e) {
      Assert.assertNull(txTestMapper.selectById(2L));
    } catch (Exception e) {
      Assert.fail("unexpect");
    }

  }

}
