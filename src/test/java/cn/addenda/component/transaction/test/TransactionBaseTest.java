package cn.addenda.component.transaction.test;

import cn.addenda.component.transaction.PlatformTransactionHelper;
import cn.addenda.component.transaction.test.mybatis.SpringConfig;
import cn.addenda.component.transaction.test.mybatis.TxTestMapper;
import lombok.SneakyThrows;
import org.junit.After;
import org.junit.Before;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;

public abstract class TransactionBaseTest {

  protected AnnotationConfigApplicationContext context;
  protected DataSource dataSource;
  protected PlatformTransactionHelper platformTransactionHelper;
  protected PlatformTransactionManager platformTransactionManager;
  protected TxTestMapper txTestMapper;
  protected TransactionTemplate transactionTemplate;

  @Before
  public void before() {
    context = new AnnotationConfigApplicationContext(SpringConfig.class);
    dataSource = context.getBean(DataSource.class);
    platformTransactionHelper = context.getBean(PlatformTransactionHelper.class);
    platformTransactionManager = context.getBean(PlatformTransactionManager.class);
    txTestMapper = context.getBean(TxTestMapper.class);
    transactionTemplate = context.getBean(TransactionTemplate.class);
    dropTxTestTable();
    createTxTestTable();
  }

  private static String create_ddl =
          "create table t_tx_test\n" +
                  "(\n" +
                  "    id           bigint      not null\n" +
                  "        primary key,\n" +
                  "    name     varchar(200) not null,\n" +
                  "    remark   varchar(500) null\n" +
                  ")";


  private static String drop_ddl = "drop table if exists t_tx_test";

  @SneakyThrows
  public void createTxTestTable() {
    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(create_ddl)) {
      preparedStatement.executeUpdate();
    }
  }

  @SneakyThrows
  public void dropTxTestTable() {
    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(drop_ddl)) {
      preparedStatement.executeUpdate();
    }
  }

  @After
  public void after() {
    context.close();
  }

}
