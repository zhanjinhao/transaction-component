package cn.addenda.component.transaction.test.mybatis;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author addenda
 * @since 2022/2/26 15:06
 */
@Setter
@Getter
@ToString
public class TxTest {

  private Long id;

  private String name;

  private String remark;

  public TxTest() {
  }

  public TxTest(Long id, String name, String remark) {
    this.id = id;
    this.name = name;
    this.remark = remark;
  }

}
