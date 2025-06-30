package cn.addenda.component.transaction;

import cn.addenda.component.base.exception.SystemException;

/**
 * @author addenda
 * @since 2022/3/2 23:04
 */
public class TransactionException extends SystemException {

  public TransactionException() {
    super();
  }

  public TransactionException(String message) {
    super(message);
  }

  public TransactionException(String message, Throwable cause) {
    super(message, cause);
  }

  public TransactionException(Throwable cause) {
    super(cause);
  }

  public TransactionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  @Override
  public String moduleName() {
    return "transaction";
  }

  @Override
  public String componentName() {
    return "transaction";
  }
}
