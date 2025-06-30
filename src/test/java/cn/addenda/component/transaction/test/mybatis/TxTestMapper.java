package cn.addenda.component.transaction.test.mybatis;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

/**
 * @author addenda
 * @since 2020/7/27
 */
public interface TxTestMapper {

  @Insert("insert into t_tx_test(id, name, remark)\n" +
          "        values (#{id,jdbcType=INTEGER}, #{name,jdbcType=VARCHAR}, #{remark,jdbcType=VARCHAR})")
  Integer insert(TxTest txTest);

  @Select("select * from t_tx_test where id = #{id,jdbcType=INTEGER}")
  TxTest selectById(long id);
}
