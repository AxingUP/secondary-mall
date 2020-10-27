package cyx.secondary.mall.controller.mall;

import cyx.secondary.mall.common.Constants;
import cyx.secondary.mall.common.ServiceResultEnum;
import cyx.secondary.mall.controller.vo.SecondaryMallShoppingCartItemVO;
import cyx.secondary.mall.controller.vo.SecondaryMallUserVO;
import cyx.secondary.mall.entity.SecondaryMallShoppingCartItem;
import cyx.secondary.mall.service.SecondaryMallShoppingCartService;
import cyx.secondary.mall.util.Result;
import cyx.secondary.mall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ShoppingCartController {

    @Resource
    private SecondaryMallShoppingCartService secondaryMallShoppingCartService;

    @GetMapping("/shop-cart")
    public String cartListPage(HttpServletRequest request,
                               HttpSession httpSession) {
        SecondaryMallUserVO user = (SecondaryMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        int itemsTotal = 0;
        int priceTotal = 0;
        List<SecondaryMallShoppingCartItemVO> myShoppingCartItems = secondaryMallShoppingCartService.getMyShoppingCartItems(user.getUserId());
        if (!CollectionUtils.isEmpty(myShoppingCartItems)) {
            //购物项总数
            itemsTotal = myShoppingCartItems.stream().mapToInt(SecondaryMallShoppingCartItemVO::getGoodsCount).sum();
            if (itemsTotal < 1) {
                return "error/error_5xx";
            }
            //总价
            for (SecondaryMallShoppingCartItemVO secondaryMallShoppingCartItemVO : myShoppingCartItems) {
                priceTotal += secondaryMallShoppingCartItemVO.getGoodsCount() * secondaryMallShoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1) {
                return "error/error_5xx";
            }
        }
        request.setAttribute("itemsTotal", itemsTotal);
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", myShoppingCartItems);
        return "mall/cart";
    }

    @PostMapping("/shop-cart")
    @ResponseBody
    public Result saveSecondaryMallShoppingCartItem(@RequestBody SecondaryMallShoppingCartItem secondaryMallShoppingCartItem,
                                                 HttpSession httpSession) {
        SecondaryMallUserVO user = (SecondaryMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        secondaryMallShoppingCartItem.setUserId(user.getUserId());
        String saveResult = secondaryMallShoppingCartService.saveSecondaryMallCartItem(secondaryMallShoppingCartItem);
        //添加成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(saveResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //添加失败
        return ResultGenerator.genFailResult(saveResult);
    }

    @PutMapping("/shop-cart")
    @ResponseBody
    public Result updateSecondaryMallShoppingCartItem(@RequestBody SecondaryMallShoppingCartItem secondaryMallShoppingCartItem,
                                                   HttpSession httpSession) {
        SecondaryMallUserVO user = (SecondaryMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        secondaryMallShoppingCartItem.setUserId(user.getUserId());
        String updateResult = secondaryMallShoppingCartService.updateSecondaryMallCartItem(secondaryMallShoppingCartItem);
        //修改成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(updateResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //修改失败
        return ResultGenerator.genFailResult(updateResult);
    }

    @DeleteMapping("/shop-cart/{secondaryMallShoppingCartItemId}")
    @ResponseBody
    public Result updateSecondaryMallShoppingCartItem(@PathVariable("secondaryMallShoppingCartItemId") Long secondaryMallShoppingCartItemId,
                                                   HttpSession httpSession) {
        SecondaryMallUserVO user = (SecondaryMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        Boolean deleteResult = secondaryMallShoppingCartService.deleteById(secondaryMallShoppingCartItemId);
        //删除成功
        if (deleteResult) {
            return ResultGenerator.genSuccessResult();
        }
        //删除失败
        return ResultGenerator.genFailResult(ServiceResultEnum.OPERATE_ERROR.getResult());
    }

    @GetMapping("/shop-cart/settle")
    public String settlePage(HttpServletRequest request,
                             HttpSession httpSession) {
        int priceTotal = 0;
        SecondaryMallUserVO user = (SecondaryMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        List<SecondaryMallShoppingCartItemVO> myShoppingCartItems = secondaryMallShoppingCartService.getMyShoppingCartItems(user.getUserId());
        if (CollectionUtils.isEmpty(myShoppingCartItems)) {
            //无数据则不跳转至结算页
            return "/shop-cart";
        } else {
            //总价
            for (SecondaryMallShoppingCartItemVO secondaryMallShoppingCartItemVO : myShoppingCartItems) {
                priceTotal += secondaryMallShoppingCartItemVO.getGoodsCount() * secondaryMallShoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1) {
                return "error/error_5xx";
            }
        }
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", myShoppingCartItems);
        return "mall/order-settle";
    }
}
