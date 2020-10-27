package cyx.secondary.mall.service;

import cyx.secondary.mall.controller.vo.SecondaryMallShoppingCartItemVO;
import cyx.secondary.mall.entity.SecondaryMallShoppingCartItem;

import java.util.List;

public interface SecondaryMallShoppingCartService {
    /**
     * 保存商品至购物车中
     *
     * @param secondaryMallShoppingCartItem
     * @return
     */
    String saveSecondaryMallCartItem(SecondaryMallShoppingCartItem secondaryMallShoppingCartItem);

    /**
     * 修改购物车中的属性
     *
     * @param secondaryMallShoppingCartItem
     * @return
     */
    String updateSecondaryMallCartItem(SecondaryMallShoppingCartItem secondaryMallShoppingCartItem);

    /**
     * 获取购物项详情
     *
     * @param secondaryMallShoppingCartItemId
     * @return
     */
    SecondaryMallShoppingCartItem getSecondaryMallCartItemById(Long secondaryMallShoppingCartItemId);

    /**
     * 删除购物车中的商品
     *
     * @param secondaryMallShoppingCartItemId
     * @return
     */
    Boolean deleteById(Long secondaryMallShoppingCartItemId);

    /**
     * 获取我的购物车中的列表数据
     *
     * @param secondaryMallUserId
     * @return
     */
    List<SecondaryMallShoppingCartItemVO> getMyShoppingCartItems(Long secondaryMallUserId);
}
