package cn.addenda.component.transaction.test;

import cn.addenda.component.transaction.TransactionAttrBuilder;
import cn.addenda.component.transaction.TransactionHelperAttrSource;
import org.springframework.transaction.interceptor.TransactionAttribute;

/**
 * @author addenda
 * @since 2023/1/7 14:12
 */
public class TransactionHelperAttributeSourceTest {

  public static void main(String[] args) {
    TransactionHelperAttrSource transactionHelperAttrSource1 = new TransactionHelperAttrSource();

    TransactionAttribute transactionAttribute1 = TransactionAttrBuilder.newRRBuilder().build();
    transactionHelperAttrSource1.pushAttr(transactionAttribute1);
    TransactionAttribute transactionAttribute2 = TransactionAttrBuilder.newRCBuilder().build();
    transactionHelperAttrSource1.pushAttr(transactionAttribute2);

    TransactionHelperAttrSource transactionHelperAttrSource2 = new TransactionHelperAttrSource();
    System.out.println(transactionHelperAttrSource2.getTransactionAttribute(null, null));
    transactionHelperAttrSource2.popAttr();
    System.out.println(transactionHelperAttrSource2.getTransactionAttribute(null, null));
    transactionHelperAttrSource2.popAttr();
  }

}
