package cyx.secondary.mall.service.impl;

import cyx.secondary.mall.common.*;
import cyx.secondary.mall.controller.vo.*;
import cyx.secondary.mall.dao.SecondaryMallGoodsMapper;
import cyx.secondary.mall.dao.SecondaryMallOrderItemMapper;
import cyx.secondary.mall.dao.SecondaryMallOrderMapper;
import cyx.secondary.mall.dao.SecondaryMallShoppingCartItemMapper;
import cyx.secondary.mall.entity.SecondaryMallGoods;
import cyx.secondary.mall.entity.SecondaryMallOrder;
import cyx.secondary.mall.entity.SecondaryMallOrderItem;
import cyx.secondary.mall.entity.StockNumDTO;
import cyx.secondary.mall.service.SecondaryMallOrderService;
import cyx.secondary.mall.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class SecondaryMallOrderServiceImpl implements SecondaryMallOrderService {

    @Autowired
    private SecondaryMallOrderMapper secondaryMallOrderMapper;
    @Autowired
    private SecondaryMallOrderItemMapper secondaryMallOrderItemMapper;
    @Autowired
    private SecondaryMallShoppingCartItemMapper secondaryMallShoppingCartItemMapper;
    @Autowired
    private SecondaryMallGoodsMapper secondaryMallGoodsMapper;

    @Override
    public PageResult getSecondaryMallOrdersPage(PageQueryUtil pageUtil) {
        List<SecondaryMallOrder> secondaryMallOrders = secondaryMallOrderMapper.findSecondaryMallOrderList(pageUtil);
        int total = secondaryMallOrderMapper.getTotalSecondaryMallOrders(pageUtil);
        PageResult pageResult = new PageResult(secondaryMallOrders, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    //用户订单分页
//    @Override
//    public PageResult getUserOrdersPage(PageQueryUserUtil pageUserUtil) {
//        List<SecondaryMallOrder> secondaryMallOrders = secondaryMallOrderMapper.findUserOrderList(pageUserUtil);
//        int total = secondaryMallOrderMapper.getTotalUserOrders(pageUserUtil);
//        PageResult pageResult = new PageResult(secondaryMallOrders, total, pageUserUtil.getLimit(), pageUserUtil.getPage());
//        return pageResult;
//    }

    @Override
    @Transactional
    public String updateOrderInfo(SecondaryMallOrder secondaryMallOrder) {
        SecondaryMallOrder temp = secondaryMallOrderMapper.selectByPrimaryKey(secondaryMallOrder.getOrderId());
        //不为空且orderStatus>=0且状态为出库之前可以修改部分信息
        if (temp != null && temp.getOrderStatus() >= 0 && temp.getOrderStatus() < 3) {
            temp.setTotalPrice(secondaryMallOrder.getTotalPrice());
            temp.setUserAddress(secondaryMallOrder.getUserAddress());
            temp.setUpdateTime(new Date());
            if (secondaryMallOrderMapper.updateByPrimaryKeySelective(temp) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            }
            return ServiceResultEnum.DB_ERROR.getResult();
        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkDone(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<SecondaryMallOrder> orders = secondaryMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (SecondaryMallOrder secondaryMallOrder : orders) {
                if (secondaryMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += secondaryMallOrder.getOrderNo() + " ";
                    continue;
                }
                if (secondaryMallOrder.getOrderStatus() != 1) {
                    errorOrderNos += secondaryMallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行配货完成操作 修改订单状态和更新时间
                if (secondaryMallOrderMapper.checkDone(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功的订单，无法执行配货完成操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkOut(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<SecondaryMallOrder> orders = secondaryMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (SecondaryMallOrder secondaryMallOrder : orders) {
                if (secondaryMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += secondaryMallOrder.getOrderNo() + " ";
                    continue;
                }
                if (secondaryMallOrder.getOrderStatus() != 1 && secondaryMallOrder.getOrderStatus() != 2) {
                    errorOrderNos += secondaryMallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行出库操作 修改订单状态和更新时间
                if (secondaryMallOrderMapper.checkOut(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功或配货完成无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功或配货完成的订单，无法执行出库操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String closeOrder(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<SecondaryMallOrder> orders = secondaryMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (SecondaryMallOrder secondaryMallOrder : orders) {
                // isDeleted=1 一定为已关闭订单
                if (secondaryMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += secondaryMallOrder.getOrderNo() + " ";
                    continue;
                }
                //已关闭或者已完成无法关闭订单
                if (secondaryMallOrder.getOrderStatus() == 4 || secondaryMallOrder.getOrderStatus() < 0) {
                    errorOrderNos += secondaryMallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行关闭操作 修改订单状态和更新时间
                if (secondaryMallOrderMapper.closeOrder(Arrays.asList(ids), SecondaryMallOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行关闭操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单不能执行关闭操作";
                } else {
                    return "你选择的订单不能执行关闭操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String saveOrder(SecondaryMallUserVO user, List<SecondaryMallShoppingCartItemVO> myShoppingCartItems) {
        List<Long> itemIdList = myShoppingCartItems.stream().map(SecondaryMallShoppingCartItemVO::getCartItemId).collect(Collectors.toList());
        List<Long> goodsIds = myShoppingCartItems.stream().map(SecondaryMallShoppingCartItemVO::getGoodsId).collect(Collectors.toList());
        List<SecondaryMallGoods> secondaryMallGoods = secondaryMallGoodsMapper.selectByPrimaryKeys(goodsIds);
        //检查是否包含已下架商品
        List<SecondaryMallGoods> goodsListNotSelling = secondaryMallGoods.stream()
                .filter(secondaryMallGoodsTemp -> secondaryMallGoodsTemp.getGoodsSellStatus() != Constants.SELL_STATUS_UP)
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(goodsListNotSelling)) {
            //goodsListNotSelling 对象非空则表示有下架商品
            SecondaryMallException.fail(goodsListNotSelling.get(0).getGoodsName() + "已下架，无法生成订单");
        }
        Map<Long, SecondaryMallGoods> secondaryMallGoodsMap = secondaryMallGoods.stream().collect(Collectors.toMap(SecondaryMallGoods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
        //判断商品库存
        for (SecondaryMallShoppingCartItemVO shoppingCartItemVO : myShoppingCartItems) {
            //查出的商品中不存在购物车中的这条关联商品数据，直接返回错误提醒
            if (!secondaryMallGoodsMap.containsKey(shoppingCartItemVO.getGoodsId())) {
                SecondaryMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
            }
            //存在数量大于库存的情况，直接返回错误提醒
            if (shoppingCartItemVO.getGoodsCount() > secondaryMallGoodsMap.get(shoppingCartItemVO.getGoodsId()).getStockNum()) {
                SecondaryMallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
            }
        }
        //删除购物项
        if (!CollectionUtils.isEmpty(itemIdList) && !CollectionUtils.isEmpty(goodsIds) && !CollectionUtils.isEmpty(secondaryMallGoods)) {
            if (secondaryMallShoppingCartItemMapper.deleteBatch(itemIdList) > 0) {
                List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(myShoppingCartItems, StockNumDTO.class);
                int updateStockNumResult = secondaryMallGoodsMapper.updateStockNum(stockNumDTOS);
                if (updateStockNumResult < 1) {
                    SecondaryMallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
                }
                //生成订单号
                String orderNo = NumberUtil.genOrderNo();
                int priceTotal = 0;
                //保存订单
                SecondaryMallOrder secondaryMallOrder = new SecondaryMallOrder();
                secondaryMallOrder.setOrderNo(orderNo);
                secondaryMallOrder.setUserId(user.getUserId());
                secondaryMallOrder.setUserAddress(user.getAddress());
                //设置添加时间
                secondaryMallOrder.setCreateTime(new Date());
                //总价
                for (SecondaryMallShoppingCartItemVO secondaryMallShoppingCartItemVO : myShoppingCartItems) {
                    priceTotal += secondaryMallShoppingCartItemVO.getGoodsCount() * secondaryMallShoppingCartItemVO.getSellingPrice();
                }
                if (priceTotal < 1) {
                    SecondaryMallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                secondaryMallOrder.setTotalPrice(priceTotal);
                //todo 订单body字段，用来作为生成支付单描述信息，暂时未接入第三方支付接口，故该字段暂时设为空字符串
                String extraInfo = "";
                secondaryMallOrder.setExtraInfo(extraInfo);
                //生成订单项并保存订单项纪录
                if (secondaryMallOrderMapper.insertSelective(secondaryMallOrder) > 0) {
                    //生成所有的订单项快照，并保存至数据库
                    List<SecondaryMallOrderItem> secondaryMallOrderItems = new ArrayList<>();
                    for (SecondaryMallShoppingCartItemVO secondaryMallShoppingCartItemVO : myShoppingCartItems) {
                        SecondaryMallOrderItem secondaryMallOrderItem = new SecondaryMallOrderItem();
                        //使用BeanUtil工具类将secondaryMallShoppingCartItemVO中的属性复制到secondaryMallOrderItem对象中
                        BeanUtil.copyProperties(secondaryMallShoppingCartItemVO, secondaryMallOrderItem);
                        //SecondaryMallOrderMapper文件insert()方法中使用了useGeneratedKeys因此orderId可以获取到
                        secondaryMallOrderItem.setOrderId(secondaryMallOrder.getOrderId());
                        secondaryMallOrderItem.setCreateTime(new Date());
                        secondaryMallOrderItems.add(secondaryMallOrderItem);
                    }
                    //保存至数据库
                    if (secondaryMallOrderItemMapper.insertBatch(secondaryMallOrderItems) > 0) {
                        //所有操作成功后，将订单号返回，以供Controller方法跳转到订单详情
                        return orderNo;
                    }
                    SecondaryMallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                SecondaryMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
            }
            SecondaryMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
        }
        SecondaryMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());

        return ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult();
    }

    @Override
    public SecondaryMallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId) {
        SecondaryMallOrder secondaryMallOrder = secondaryMallOrderMapper.selectByOrderNo(orderNo);
        if (secondaryMallOrder != null) {
            List<SecondaryMallOrderItem> orderItems = secondaryMallOrderItemMapper.selectByOrderId(secondaryMallOrder.getOrderId());
            //获取订单项数据
            if (!CollectionUtils.isEmpty(orderItems)) {
                List<SecondaryMallOrderItemVO> secondaryMallOrderItemVOS = BeanUtil.copyList(orderItems, SecondaryMallOrderItemVO.class);
                SecondaryMallOrderDetailVO secondaryMallOrderDetailVO = new SecondaryMallOrderDetailVO();
                BeanUtil.copyProperties(secondaryMallOrder, secondaryMallOrderDetailVO);
                secondaryMallOrderDetailVO.setOrderStatusString(SecondaryMallOrderStatusEnum.getSecondaryMallOrderStatusEnumByStatus(secondaryMallOrderDetailVO.getOrderStatus()).getName());
                secondaryMallOrderDetailVO.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(secondaryMallOrderDetailVO.getPayType()).getName());
                secondaryMallOrderDetailVO.setSecondaryMallOrderItemVOS(secondaryMallOrderItemVOS);
                return secondaryMallOrderDetailVO;
            }
        }
        return null;
    }

    @Override
    public SecondaryMallOrder getSecondaryMallOrderByOrderNo(String orderNo) {
        return secondaryMallOrderMapper.selectByOrderNo(orderNo);
    }

    @Override
    public PageResult getMyOrders(PageQueryUtil pageUtil) {
        int total = secondaryMallOrderMapper.getTotalSecondaryMallOrders(pageUtil);
        List<SecondaryMallOrder> secondaryMallOrders = secondaryMallOrderMapper.findSecondaryMallOrderList(pageUtil);
        List<SecondaryMallOrderListVO> orderListVOS = new ArrayList<>();
        if (total > 0) {
            //数据转换 将实体类转成vo
            orderListVOS = BeanUtil.copyList(secondaryMallOrders, SecondaryMallOrderListVO.class);
            //设置订单状态中文显示值
            for (SecondaryMallOrderListVO secondaryMallOrderListVO : orderListVOS) {
                secondaryMallOrderListVO.setOrderStatusString(SecondaryMallOrderStatusEnum.getSecondaryMallOrderStatusEnumByStatus(secondaryMallOrderListVO.getOrderStatus()).getName());
            }
            List<Long> orderIds = secondaryMallOrders.stream().map(SecondaryMallOrder::getOrderId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(orderIds)) {
                List<SecondaryMallOrderItem> orderItems = secondaryMallOrderItemMapper.selectByOrderIds(orderIds);
                Map<Long, List<SecondaryMallOrderItem>> itemByOrderIdMap = orderItems.stream().collect(groupingBy(SecondaryMallOrderItem::getOrderId));
                for (SecondaryMallOrderListVO secondaryMallOrderListVO : orderListVOS) {
                    //封装每个订单列表对象的订单项数据
                    if (itemByOrderIdMap.containsKey(secondaryMallOrderListVO.getOrderId())) {
                        List<SecondaryMallOrderItem> orderItemListTemp = itemByOrderIdMap.get(secondaryMallOrderListVO.getOrderId());
                        //将SecondaryMallOrderItem对象列表转换成SecondaryMallOrderItemVO对象列表
                        List<SecondaryMallOrderItemVO> secondaryMallOrderItemVOS = BeanUtil.copyList(orderItemListTemp, SecondaryMallOrderItemVO.class);
                        secondaryMallOrderListVO.setSecondaryMallOrderItemVOS(secondaryMallOrderItemVOS);
                    }
                }
            }
        }
        PageResult pageResult = new PageResult(orderListVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String cancelOrder(String orderNo, Long userId) {
        SecondaryMallOrder secondaryMallOrder = secondaryMallOrderMapper.selectByOrderNo(orderNo);
        if (secondaryMallOrder != null) {
            //todo 验证是否是当前userId下的订单，否则报错
            //todo 订单状态判断
            if (secondaryMallOrderMapper.closeOrder(Collections.singletonList(secondaryMallOrder.getOrderId()), SecondaryMallOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String finishOrder(String orderNo, Long userId) {
        SecondaryMallOrder secondaryMallOrder = secondaryMallOrderMapper.selectByOrderNo(orderNo);
        if (secondaryMallOrder != null) {
            //todo 验证是否是当前userId下的订单，否则报错
            //todo 订单状态判断
            secondaryMallOrder.setOrderStatus((byte) SecondaryMallOrderStatusEnum.ORDER_SUCCESS.getOrderStatus());
            secondaryMallOrder.setUpdateTime(new Date());
            if (secondaryMallOrderMapper.updateByPrimaryKeySelective(secondaryMallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String paySuccess(String orderNo, int payType) {
        SecondaryMallOrder secondaryMallOrder = secondaryMallOrderMapper.selectByOrderNo(orderNo);
        if (secondaryMallOrder != null) {
            //todo 订单状态判断 非待支付状态下不进行修改操作
            secondaryMallOrder.setOrderStatus((byte) SecondaryMallOrderStatusEnum.OREDER_PAID.getOrderStatus());
            secondaryMallOrder.setPayType((byte) payType);
            secondaryMallOrder.setPayStatus((byte) PayStatusEnum.PAY_SUCCESS.getPayStatus());
            secondaryMallOrder.setPayTime(new Date());
            secondaryMallOrder.setUpdateTime(new Date());
            if (secondaryMallOrderMapper.updateByPrimaryKeySelective(secondaryMallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public List<SecondaryMallOrderItemVO> getOrderItems(Long id) {
        SecondaryMallOrder secondaryMallOrder = secondaryMallOrderMapper.selectByPrimaryKey(id);
        if (secondaryMallOrder != null) {
            List<SecondaryMallOrderItem> orderItems = secondaryMallOrderItemMapper.selectByOrderId(secondaryMallOrder.getOrderId());
            //获取订单项数据
            if (!CollectionUtils.isEmpty(orderItems)) {
                List<SecondaryMallOrderItemVO> secondaryMallOrderItemVOS = BeanUtil.copyList(orderItems, SecondaryMallOrderItemVO.class);
                return secondaryMallOrderItemVOS;
            }
        }
        return null;
    }

    //用户出售
    @Override
    public PageResult getUserOrderByUid(PageQueryUserUtil pageUserUtil) {

//        List<SecondaryMallOrder> secondaryMallOrders = secondaryMallOrderMapper.findUserOrderList(pageUserUtil);
//        int total = secondaryMallOrderMapper.getTotalUserOrders(pageUserUtil);
//        PageResult pageResult = new PageResult(secondaryMallOrders, total, pageUserUtil.getLimit(), pageUserUtil.getPage());
//        return pageResult;

        List<SecondaryMallOrder> secondaryMallOrders = secondaryMallOrderMapper.selectUserOrdersByUid(pageUserUtil);
        int total = secondaryMallOrderMapper.getTotalUserOrdersByUid(pageUserUtil);
        System.out.println(secondaryMallOrders);
        PageResult pageResult = new PageResult(secondaryMallOrders, total, pageUserUtil.getLimit(), pageUserUtil.getPage());
        return pageResult;
       // return list;
    }
}
