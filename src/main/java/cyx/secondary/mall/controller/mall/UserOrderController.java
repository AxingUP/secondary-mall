package cyx.secondary.mall.controller.mall;

import cyx.secondary.mall.common.Constants;
import cyx.secondary.mall.common.ServiceResultEnum;
import cyx.secondary.mall.controller.vo.SecondaryMallOrderItemVO;
import cyx.secondary.mall.controller.vo.SecondaryMallUserVO;
import cyx.secondary.mall.entity.SecondaryMallOrder;
import cyx.secondary.mall.service.SecondaryMallOrderService;
import cyx.secondary.mall.util.PageQueryUserUtil;
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
import java.util.Objects;

@Controller
@RequestMapping("/mall")
public class UserOrderController {
    @Resource
    private SecondaryMallOrderService secondaryMallOrderService;

    @GetMapping("/orders")
    public String ordersPage(HttpServletRequest request) {
        request.setAttribute("path", "orders");
        return "admin/user_order";
    }

    /**
     * 列表
     */
    //用户出售
    @RequestMapping(value = "/orders/list", method = RequestMethod.GET)
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params, HttpSession httpSession) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
//        PageQueryUserUtil pageUserUtil = new PageQueryUserUtil(params);
//        SecondaryMallUserVO secondaryMallUserVO = (SecondaryMallUserVO)httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
//        pageUserUtil.setUid(secondaryMallUserVO.getUserId());
//        System.out.println("用户出售");
        SecondaryMallUserVO secondaryMallUserVO = (SecondaryMallUserVO)httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        params.put("uid",secondaryMallUserVO.getUserId());
        PageQueryUserUtil pageUserUtil = new PageQueryUserUtil(params);
        return ResultGenerator.genSuccessResult(secondaryMallOrderService.getUserOrderByUid(pageUserUtil));


//        System.out.println(secondaryMallUserVO.getUserId());
//
//
//        PageQueryUserUtil pageUserUtil = new PageQueryUserUtil(params);
//
//        return ResultGenerator.genSuccessResult(secondaryMallOrderService.getUserOrdersPage(pageUserUtil));
    }

    /**
     * 修改
     */
    @RequestMapping(value = "/orders/update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody SecondaryMallOrder secondaryMallOrder) {
        if (Objects.isNull(secondaryMallOrder.getTotalPrice())
                || Objects.isNull(secondaryMallOrder.getOrderId())
                || secondaryMallOrder.getOrderId() < 1
                || secondaryMallOrder.getTotalPrice() < 1
                || StringUtils.isEmpty(secondaryMallOrder.getUserAddress())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = secondaryMallOrderService.updateOrderInfo(secondaryMallOrder);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 详情
     */
    @GetMapping("/order-items/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Long id) {
        List<SecondaryMallOrderItemVO> orderItems = secondaryMallOrderService.getOrderItems(id);
        if (!CollectionUtils.isEmpty(orderItems)) {
            return ResultGenerator.genSuccessResult(orderItems);
        }
        return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
    }

    /**
     * 配货
     */
    @RequestMapping(value = "/orders/checkDone", method = RequestMethod.POST)
    @ResponseBody
    public Result checkDone(@RequestBody Long[] ids) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = secondaryMallOrderService.checkDone(ids);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 出库
     */
    @RequestMapping(value = "/orders/checkOut", method = RequestMethod.POST)
    @ResponseBody
    public Result checkOut(@RequestBody Long[] ids) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = secondaryMallOrderService.checkOut(ids);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 关闭订单
     */
    @RequestMapping(value = "/orders/close", method = RequestMethod.POST)
    @ResponseBody
    public Result closeOrder(@RequestBody Long[] ids) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = secondaryMallOrderService.closeOrder(ids);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }
}
