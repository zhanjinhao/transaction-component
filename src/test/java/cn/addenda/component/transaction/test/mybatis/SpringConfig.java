package cn.addenda.component.transaction.test.mybatis;

import cn.addenda.component.transaction.PlatformTransactionHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

@Configuration
@ComponentScan("cn.addenda.component.transaction.test")
@Import({MybatisConfig.class})
public class SpringConfig {

  @Bean
  public DataSource dataSource() {
    return DruidDataSourceManager.getDataSource("transaction-component");
  }

  @Bean
  public PlatformTransactionManager platformTransactionManager(DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }

  @Bean
  public PlatformTransactionHelper platformTransactionHelper(PlatformTransactionManager platformTransactionManager) {
    return new PlatformTransactionHelper(platformTransactionManager);
  }

  @Bean
  public TransactionTemplate transactionTemplate(PlatformTransactionManager platformTransactionManager) {
    return new TransactionTemplate(platformTransactionManager);
  }

}
