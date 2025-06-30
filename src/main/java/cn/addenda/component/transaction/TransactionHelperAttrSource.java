package cn.addenda.component.transaction;

import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;

import java.lang.reflect.Method;
import java.util.Stack;

/**
 * @author addenda
 * @since 2022/3/3 17:26
 */
public class TransactionHelperAttrSource implements TransactionAttributeSource {

  private final ThreadLocal<Stack<TransactionAttribute>> transactionLocal = new ThreadLocal<>();

  @Override
  public TransactionAttribute getTransactionAttribute(Method method, Class<?> targetClass) {
    return transactionLocal.get().peek();
  }

  public void pushAttr(TransactionAttribute transactionAttribute) {
    synchronized (this) {
      Stack<TransactionAttribute> transactionAttributes = transactionLocal.get();
      if (transactionAttributes == null) {
        transactionAttributes = new Stack<>();
        transactionLocal.set(transactionAttributes);
      }
      transactionAttributes.push(transactionAttribute);
    }
  }

  public void popAttr() {
    synchronized (this) {
      Stack<TransactionAttribute> transactionAttributes = transactionLocal.get();
      transactionAttributes.pop();
      if (transactionAttributes.isEmpty()) {
        transactionLocal.remove();
      }
    }
  }

  @Override
  public String toString() {
    return "TransactionHelperAttrSource{" +
            "transactionLocal=" + transactionLocal +
            '}';
  }
}
