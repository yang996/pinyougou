import com.alibaba.dao.CustomerDao;
import com.alibaba.domain.Customer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ClassName CustomerTest
 * @Author WuYeYang
 * @Description
 * @Date 2018/11/24 20:02
 * @Version 1.0
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class CustomerTest {

    @Autowired
    private CustomerDao customerDao;

    /**
     * 保存客户
     */
    @Test
    public void testSave(){
        Customer c=new Customer();
        c.setCustName("腾讯");
        customerDao.save(c);
    }

    /**
     * 更新
     */
    @Test
    public void testUpdate(){
        Customer c=new Customer();
        c.setCustId(4L);
        c.setCustName("阿里巴巴");
        customerDao.save(c);
    }

    /**
     * 根据id删除
     */
    @Test
    public void testDelete(){
        customerDao.delete(6L);
    }

    /**
     * 根据id查询,调用findOne方法
     */
    @Test
    public void testFindOne(){
        Customer customer = customerDao.findOne(4L);
        System.out.println(customer);
    }

    /**
     * 使用jpql方式查询所有
     */
    @Test
    public void testFindAll(){
        List<Customer> customerList =
                customerDao.findAllCustomer();
        for (Customer customer : customerList) {
            System.out.println(customer);
        }
    }

    /**
     * 使用jpql查询指定实体
     */
    @Test
    public void testFindCustomer(){
        Customer customer = customerDao.findCustomer("阿里巴巴");
        System.out.println(customer);
    }

    /**
     * 使用jpql进行更新操作
     */
    @Test
    @Transactional
    @Rollback(value = false)
    public void updateCustomer(){
        customerDao.updateCustomer("百度",5L);
    }

    /**
     * 使用sql语句查询
     */
    @Test
    public void findBySql(){
        List<Customer> customerList = customerDao.findSql();
        for (Customer customer : customerList) {
            System.out.println(customer);
        }
    }

    /**
     * 方法命名方式查询
     */
    @Test
    public void findByMethodName(){
        customerDao.findByCustName("阿里巴巴");
    }
}
