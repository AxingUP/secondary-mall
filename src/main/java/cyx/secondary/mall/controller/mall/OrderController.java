package cyx.secondary.mall.controller.mall;

import cyx.secondary.mall.common.Constants;
import cyx.secondary.mall.common.SecondaryMallException;
import cyx.secondary.mall.common.ServiceResultEnum;
import cyx.secondary.mall.controller.vo.SecondaryMallOrderDetailVO;
import cyx.secondary.mall.controller.vo.SecondaryMallShoppingCartItemVO;
import cyx.secondary.mall.controller.vo.SecondaryMallUserVO;
import cyx.secondary.mall.entity.SecondaryMallOrder;
import cyx.secondary.mall.service.SecondaryMallOrderService;
import cyx.secondary.mall.service.SecondaryMallShoppingCartService;
import cyx.secondary.mall.util.PageQueryUtil;
import cyx.secondary.mall.util.Result;
import cyx.secondary.mall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController {
    @Resource
    private SecondaryMallShoppingCartService secondaryMallShoppingCartService;
    @Resource
    private SecondaryMallOrderService secondaryMallOrderService;

    @GetMapping("/orders/{orderNo}")
    public String orderDetailPage(HttpServletRequest request, @PathVariable("orderNo") String orderNo, HttpSession httpSession) {
        SecondaryMallUserVO user = (SecondaryMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        SecondaryMallOrderDetailVO orderDetailVO = secondaryMallOrderService.getOrderDetailByOrderNo(orderNo, user.getUserId());
        if (orderDetailVO == null) {
            return "error/error_5xx";
        }
        request.setAttribute("orderDetailVO", orderDetailVO);
        return "mall/order-detail";
    }

    @GetMapping("/orders")
    public String orderListPage(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpSession httpSession) {
        SecondaryMallUserVO user = (SecondaryMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        params.put("userId", user.getUserId());
        if (StringUtils.isEmpty(params.get("page"))) {
            params.put("page", 1);
        }
        params.put("limit", Constants.ORDER_SEARCH_PAGE_LIMIT);
        //封装我的订单数据
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        request.setAttribute("orderPageResult", secondaryMallOrderService.getMyOrders(pageUtil));
        request.setAttribute("path", "orders");
        return "mall/my-orders";
    }

    @GetMapping("/saveOrder")
    public String saveOrder(HttpSession httpSession) {
        SecondaryMallUserVO user = (SecondaryMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        List<SecondaryMallShoppingCartItemVO> myShoppingCartItems = secondaryMallShoppingCartService.getMyShoppingCartItems(user.getUserId());
        if (StringUtils.isEmpty(user.getAddress().trim())) {
            //无收货地址
            SecondaryMallException.fail(ServiceResultEnum.NULL_ADDRESS_ERROR.getResult());
        }
        if (CollectionUtils.isEmpty(myShoppingCartItems)) {
            //购物车中无数据则跳转至错误页
            SecondaryMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        }
        //保存订单并返回订单号
        String saveOrderResult = secondaryMallOrderService.saveOrder(user, myShoppingCartItems);
        //跳转到订单详情页
        return "redirect:/orders/" + saveOrderResult;
    }

    @PutMapping("/orders/{orderNo}/cancel")
    @ResponseBody
    public Result cancelOrder(@PathVariable("orderNo") String orderNo, HttpSession httpSession) {
        SecondaryMallUserVO user = (SecondaryMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        String cancelOrderResult = secondaryMallOrderService.cancelOrder(orderNo, user.getUserId());
        if (ServiceResultEnum.SUCCESS.getResult().equals(cancelOrderResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(cancelOrderResult);
        }
    }

    @PutMapping("/orders/{orderNo}/finish")
    @ResponseBody
    public Result finishOrder(@PathVariable("orderNo") String orderNo, HttpSession httpSession) {
        SecondaryMallUserVO user = (SecondaryMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        String finishOrderResult = secondaryMallOrderService.finishOrder(orderNo, user.getUserId());
        if (ServiceResultEnum.SUCCESS.getResult().equals(finishOrderResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(finishOrderResult);
        }
    }

    @GetMapping("/selectPayType")
    public String selectPayType(HttpServletRequest request, @RequestParam("orderNo") String orderNo, HttpSession httpSession) {
        SecondaryMallUserVO user = (SecondaryMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        SecondaryMallOrder secondaryMallOrder = secondaryMallOrderService.getSecondaryMallOrderByOrderNo(orderNo);
        //todo 判断订单userId
        //todo 判断订单状态
        request.setAttribute("orderNo", orderNo);
        request.setAttribute("totalPrice", secondaryMallOrder.getTotalPrice());
        return "mall/pay-select";
    }

    @GetMapping("/payPage")
    public String payOrder(HttpServletRequest request, @RequestParam("orderNo") String orderNo, HttpSession httpSession, @RequestParam("payType") int payType) {
        SecondaryMallUserVO user = (SecondaryMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        SecondaryMallOrder secondaryMallOrder = secondaryMallOrderService.getSecondaryMallOrderByOrderNo(orderNo);
        //todo 判断订单userId
        //todo 判断订单状态
        request.setAttribute("orderNo", orderNo);
        request.setAttribute("totalPrice", secondaryMallOrder.getTotalPrice());
        if (payType == 1) {
            return "mall/alipay";
        } else {
            return "mall/wxpay";
        }
    }

    @GetMapping("/paySuccess")
    @ResponseBody
    public Result paySuccess(@RequestParam("orderNo") String orderNo, @RequestParam("payType") int payType) {
        String payResult = secondaryMallOrderService.paySuccess(orderNo, payType);
        if (ServiceResultEnum.SUCCESS.getResult().equals(payResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(payResult);
        }
    }
}

