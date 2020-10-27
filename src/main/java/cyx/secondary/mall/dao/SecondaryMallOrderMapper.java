package cyx.secondary.mall.dao;

import cyx.secondary.mall.entity.SecondaryMallOrder;
import cyx.secondary.mall.util.PageQueryUserUtil;
import cyx.secondary.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SecondaryMallOrderMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(SecondaryMallOrder record);

    int insertSelective(SecondaryMallOrder record);

    SecondaryMallOrder selectByPrimaryKey(Long orderId);

    SecondaryMallOrder selectByOrderNo(String orderNo);

    int updateByPrimaryKeySelective(SecondaryMallOrder record);

    int updateByPrimaryKey(SecondaryMallOrder record);

    List<SecondaryMallOrder> findSecondaryMallOrderList(PageQueryUtil pageUtil);

    //用户出售
    List<SecondaryMallOrder> selectUserOrdersByUid(PageQueryUserUtil pageUserUtil);

    //用户出售
    int getTotalUserOrdersByUid(PageQueryUserUtil pageUserUtil);



    //用户订单分页
    List<SecondaryMallOrder> findUserOrderList(PageQueryUserUtil pageUserUtil);


    int getTotalSecondaryMallOrders(PageQueryUtil pageUtil);

    //用户订单页数
    int getTotalUserOrders(PageQueryUserUtil pageUserUtil);


    List<SecondaryMallOrder> selectByPrimaryKeys(@Param("orderIds") List<Long> orderIds);

    int checkOut(@Param("orderIds") List<Long> orderIds);

    int closeOrder(@Param("orderIds") List<Long> orderIds, @Param("orderStatus") int orderStatus);

    int checkDone(@Param("orderIds") List<Long> asList);
}
