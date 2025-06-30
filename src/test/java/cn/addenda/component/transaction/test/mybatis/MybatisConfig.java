package cn.addenda.component.transaction.test.mybatis;

import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

public class MybatisConfig {

  @Bean
  public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource) {
    SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
    sqlSessionFactoryBean.setDataSource(dataSource);
    Configuration configuration = new Configuration();
    configuration.setLogImpl(StdOutImpl.class);
    sqlSessionFactoryBean.setConfiguration(configuration);
    return sqlSessionFactoryBean;
  }

  @Bean
  public MapperScannerConfigurer mapperScannerConfigurer() {
    MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
    mapperScannerConfigurer.setBasePackage("cn.addenda.component.transaction.test.mybatis");
    return mapperScannerConfigurer;
  }

}
