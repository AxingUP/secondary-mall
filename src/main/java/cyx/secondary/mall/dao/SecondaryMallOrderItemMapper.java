package cyx.secondary.mall.dao;

import cyx.secondary.mall.entity.SecondaryMallOrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SecondaryMallOrderItemMapper {
    int deleteByPrimaryKey(Long orderItemId);

    int insert(SecondaryMallOrderItem record);

    int insertSelective(SecondaryMallOrderItem record);

    SecondaryMallOrderItem selectByPrimaryKey(Long orderItemId);

    /**
     * 根据订单id获取订单项列表
     *
     * @param orderId
     * @return
     */
    List<SecondaryMallOrderItem> selectByOrderId(Long orderId);

    /**
     * 根据订单ids获取订单项列表
     *
     * @param orderIds
     * @return
     */
    List<SecondaryMallOrderItem> selectByOrderIds(@Param("orderIds") List<Long> orderIds);

    /**
     * 批量insert订单项数据
     *
     * @param orderItems
     * @return
     */
    int insertBatch(@Param("orderItems") List<SecondaryMallOrderItem> orderItems);

    int updateByPrimaryKeySelective(SecondaryMallOrderItem record);

    int updateByPrimaryKey(SecondaryMallOrderItem record);
}
