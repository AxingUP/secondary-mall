package cyx.secondary.mall.service;

import cyx.secondary.mall.controller.vo.SecondaryMallOrderDetailVO;
import cyx.secondary.mall.controller.vo.SecondaryMallOrderItemVO;
import cyx.secondary.mall.controller.vo.SecondaryMallShoppingCartItemVO;
import cyx.secondary.mall.controller.vo.SecondaryMallUserVO;
import cyx.secondary.mall.entity.SecondaryMallOrder;
import cyx.secondary.mall.util.PageQueryUserUtil;
import cyx.secondary.mall.util.PageQueryUtil;
import cyx.secondary.mall.util.PageResult;

import java.util.List;

public interface SecondaryMallOrderService {

    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getSecondaryMallOrdersPage(PageQueryUtil pageUtil);

    //用户订单分页
//    PageResult getUserOrdersPage(PageQueryUserUtil pageUserUtil);
    /**
     * 订单信息修改
     *
     * @param secondaryMallOrder
     * @return
     */
    String updateOrderInfo(SecondaryMallOrder secondaryMallOrder);

    /**
     * 配货
     *
     * @param ids
     * @return
     */
    String checkDone(Long[] ids);

    /**
     * 出库
     *
     * @param ids
     * @return
     */
    String checkOut(Long[] ids);

    /**
     * 关闭订单
     *
     * @param ids
     * @return
     */
    String closeOrder(Long[] ids);

    /**
     * 保存订单
     *
     * @param user
     * @param myShoppingCartItems
     * @return
     */
    String saveOrder(SecondaryMallUserVO user, List<SecondaryMallShoppingCartItemVO> myShoppingCartItems);

    /**
     * 获取订单详情
     *
     * @param orderNo
     * @param userId
     * @return
     */
    SecondaryMallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId);

    /**
     * 获取订单详情
     *
     * @param orderNo
     * @return
     */
    SecondaryMallOrder getSecondaryMallOrderByOrderNo(String orderNo);

    /**
     * 我的订单列表
     *
     * @param pageUtil
     * @return
     */
    PageResult getMyOrders(PageQueryUtil pageUtil);

    /**
     * 手动取消订单
     *
     * @param orderNo
     * @param userId
     * @return
     */
    String cancelOrder(String orderNo, Long userId);

    /**
     * 确认收货
     *
     * @param orderNo
     * @param userId
     * @return
     */
    String finishOrder(String orderNo, Long userId);

    String paySuccess(String orderNo, int payType);

    List<SecondaryMallOrderItemVO> getOrderItems(Long id);

    //用户出售
    PageResult getUserOrderByUid(PageQueryUserUtil pageUserUtil);
}
