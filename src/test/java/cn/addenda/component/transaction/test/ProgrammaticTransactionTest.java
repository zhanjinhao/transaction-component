package cn.addenda.component.transaction.test;

import cn.addenda.component.transaction.test.mybatis.TxTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * @author addenda
 * @since 2023/1/5 20:22
 */
public class ProgrammaticTransactionTest extends TransactionBaseTest {

  @Test
  public void test1() {

    DefaultTransactionDefinition def = new DefaultTransactionDefinition();
    def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    TransactionStatus status = platformTransactionManager.getTransaction(def);
    try {
      txTestMapper.insert(new TxTest(1L, "setRollbackOnly", "123"));

      throw new NullPointerException();
    } catch (Exception ex) {
      // log ...
      status.setRollbackOnly();
    } finally {
      platformTransactionManager.commit(status);
    }

    Assert.assertNull(txTestMapper.selectById(1L));
  }


  @Test
  public void test2() {

    DefaultTransactionDefinition def = new DefaultTransactionDefinition();
    def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    TransactionStatus status = platformTransactionManager.getTransaction(def);
    try {
      txTestMapper.insert(new TxTest(2L, "success", "123"));
    } catch (Exception ex) {
      // log ...
      status.setRollbackOnly();
    } finally {
      platformTransactionManager.commit(status);
    }

    Assert.assertNotNull(txTestMapper.selectById(2L));
  }

}
