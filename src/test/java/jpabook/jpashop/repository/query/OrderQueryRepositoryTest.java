package jpabook.jpashop.repository.query;

import jpabook.jpashop.repository.OrderSimpleQueryDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderQueryRepositoryTest {
    @Autowired
    private OrderQueryRepository orderQueryRepository;

    @Test
    @Description("Test")
    public void findAllDto_flat() throws Exception{
        orderQueryRepository.findAllDto_flat();
    }

}