package cn.addenda.component.transaction;

import cn.addenda.component.base.exception.ExceptionUtils;
import cn.addenda.component.base.lambda.TRunnable;
import cn.addenda.component.base.lambda.TSupplier;
import cn.addenda.component.stacktrace.StackTraceUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.interceptor.TransactionAttribute;

import java.lang.reflect.Method;

/**
 * @author addenda
 * @since 2022/3/3 17:26
 */
public class PlatformTransactionHelper extends TransactionAspectSupport {

  private final PlatformTransactionManager platformTransactionManager;

  private final TransactionHelperAttrSource transactionHelperAttrSource;

  public PlatformTransactionHelper(PlatformTransactionManager platformTransactionManager) {
    this.platformTransactionManager = platformTransactionManager;
    setTransactionManager(platformTransactionManager);
    this.transactionHelperAttrSource = new TransactionHelperAttrSource();
    setTransactionAttributeSource(transactionHelperAttrSource);
  }

  /**
   * 发生Exception.class时回滚事务
   */
  public <R> R doTransaction(TSupplier<R> supplier) {
    return doTransaction(Exception.class, StackTraceUtils.getCallerInfo(true, true, true), supplier);
  }

  /**
   * 发生Exception.class时回滚事务
   */
  public void doTransaction(TRunnable runnable) {
    doTransaction(Exception.class, StackTraceUtils.getCallerInfo(true, true, true), runnable);
  }

  /**
   * 发生Exception.class时回滚事务，且指定descriptor
   */
  public <R> R doTransaction(String descriptor, TSupplier<R> supplier) {
    return doTransaction(Exception.class, descriptor, supplier);
  }

  /**
   * 发生Exception.class时回滚事务，且指定descriptor
   */
  public void doTransaction(String descriptor, TRunnable runnable) {
    doTransaction(Exception.class, descriptor, runnable);
  }

  /**
   * 发生指定异常时回滚事务
   */
  public <R> R doTransaction(Class<? extends Throwable> rollbackFor, TSupplier<R> supplier) {
    return doTransaction(rollbackFor, StackTraceUtils.getCallerInfo(true, true, true), supplier);
  }

  /**
   * 发生指定异常时回滚事务
   */
  public void doTransaction(Class<? extends Throwable> rollbackFor, TRunnable runnable) {
    doTransaction(rollbackFor, StackTraceUtils.getCallerInfo(true, true, true), runnable);
  }

  /**
   * 发生指定异常时回滚事务，且指定descriptor
   */
  public <R> R doTransaction(Class<? extends Throwable> rollbackFor, String descriptor, TSupplier<R> supplier) {
    TransactionAttribute attribute = TransactionAttrBuilder.newBuilder()
            .rollbackFor(rollbackFor)
            .withDescriptor(descriptor)
            .build();
    return doTransaction(attribute, supplier);
  }

  /**
   * 发生指定异常时回滚事务，且指定descriptor
   */
  public void doTransaction(Class<? extends Throwable> rollbackFor, String descriptor, TRunnable runnable) {
    TransactionAttribute attribute = TransactionAttrBuilder.newBuilder()
            .rollbackFor(rollbackFor)
            .withDescriptor(descriptor)
            .build();
    doTransaction(attribute, runnable);
  }

  /**
   * 最复杂的场景，需要手动指定所有的事务控制参数，TransactionAttribute 可以通过 TransactionAttributeBuilder构造
   * TransactionAttributeBuilder的入参跟@Transactional注解的参数保持一致
   */
  public <R> R doTransaction(TransactionAttribute txAttr, TSupplier<R> supplier) {
    return _process(txAttr, supplier);
  }

  public void doTransaction(TransactionAttribute txAttr, TRunnable runnable) {
    TSupplier<Object> supplier = new TSupplier<Object>() {
      @Override
      public Object get() throws Throwable {
        runnable.run();
        return null;
      }

      @Override
      public String toString() {
        return "PlatformTransactionHelper.TSupplier.Wrapper{" +
                "runnable=" + runnable +
                '}';
      }
    };
    doTransaction(txAttr, supplier);
  }

  private <R> R _process(TransactionAttribute txAttr, TSupplier<R> supplier) {
    transactionHelperAttrSource.pushAttr(txAttr);
    try {
      return (R) invokeWithinTransaction(extractMethod(supplier), supplier.getClass(), supplier::get);
    } catch (Throwable throwable) {
      throw ExceptionUtils.wrapAsRuntimeException(throwable, TransactionException.class);
    } finally {
      transactionHelperAttrSource.popAttr();
    }
  }

  private Method extractMethod(TSupplier<?> supplier) {
    Method[] methods = supplier.getClass().getMethods();
    for (Method method : methods) {
      if ("get".equals(method.getName()) && method.getParameterCount() == 0) {
        return method;
      }
    }
    throw new TransactionException("找不到 TSupplier#get() 方法。");
  }

  @Override
  public String toString() {
    return "PlatformTransactionHelper{" +
            "platformTransactionManager=" + platformTransactionManager +
            ", transactionHelperAttrSource=" + transactionHelperAttrSource +
            "} " + super.toString();
  }
}
