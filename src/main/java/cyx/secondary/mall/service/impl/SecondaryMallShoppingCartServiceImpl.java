package cyx.secondary.mall.service.impl;

import cyx.secondary.mall.common.Constants;
import cyx.secondary.mall.common.ServiceResultEnum;
import cyx.secondary.mall.controller.vo.SecondaryMallShoppingCartItemVO;
import cyx.secondary.mall.dao.SecondaryMallGoodsMapper;
import cyx.secondary.mall.dao.SecondaryMallShoppingCartItemMapper;
import cyx.secondary.mall.entity.SecondaryMallGoods;
import cyx.secondary.mall.entity.SecondaryMallShoppingCartItem;
import cyx.secondary.mall.service.SecondaryMallShoppingCartService;
import cyx.secondary.mall.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SecondaryMallShoppingCartServiceImpl implements SecondaryMallShoppingCartService {

    @Autowired
    private SecondaryMallShoppingCartItemMapper secondaryMallShoppingCartItemMapper;

    @Autowired
    private SecondaryMallGoodsMapper secondaryMallGoodsMapper;

    @Override
    public String saveSecondaryMallCartItem(SecondaryMallShoppingCartItem secondaryMallShoppingCartItem) {
        SecondaryMallShoppingCartItem temp = secondaryMallShoppingCartItemMapper.selectByUserIdAndGoodsId(secondaryMallShoppingCartItem.getUserId(), secondaryMallShoppingCartItem.getGoodsId());
        if (temp != null) {
            temp.setGoodsCount(temp.getGoodsCount() + secondaryMallShoppingCartItem.getGoodsCount());
            return updateSecondaryMallCartItem(temp);
        }
        SecondaryMallGoods secondaryMallGoods = secondaryMallGoodsMapper.selectByPrimaryKey(secondaryMallShoppingCartItem.getGoodsId());
        //商品为空
        if (secondaryMallGoods == null) {
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        int totalItem = secondaryMallShoppingCartItemMapper.selectCountByUserId(secondaryMallShoppingCartItem.getUserId());
        //超出最大数量
        if (totalItem > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        //保存记录
        if (secondaryMallShoppingCartItemMapper.insertSelective(secondaryMallShoppingCartItem) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateSecondaryMallCartItem(SecondaryMallShoppingCartItem secondaryMallShoppingCartItem) {
        SecondaryMallShoppingCartItem secondaryMallShoppingCartItemUpdate = secondaryMallShoppingCartItemMapper.selectByPrimaryKey(secondaryMallShoppingCartItem.getCartItemId());
        if (secondaryMallShoppingCartItemUpdate == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        //超出最大数量
        if (secondaryMallShoppingCartItem.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        secondaryMallShoppingCartItemUpdate.setGoodsCount(secondaryMallShoppingCartItem.getGoodsCount());
        secondaryMallShoppingCartItemUpdate.setUpdateTime(new Date());
        //修改记录
        if (secondaryMallShoppingCartItemMapper.updateByPrimaryKeySelective(secondaryMallShoppingCartItemUpdate) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public SecondaryMallShoppingCartItem getSecondaryMallCartItemById(Long secondaryMallShoppingCartItemId) {
        return secondaryMallShoppingCartItemMapper.selectByPrimaryKey(secondaryMallShoppingCartItemId);
    }

    @Override
    public Boolean deleteById(Long secondaryMallShoppingCartItemId) {
        return secondaryMallShoppingCartItemMapper.deleteByPrimaryKey(secondaryMallShoppingCartItemId) > 0;
    }

    @Override
    public List<SecondaryMallShoppingCartItemVO> getMyShoppingCartItems(Long secondaryMallUserId) {
        List<SecondaryMallShoppingCartItemVO> secondaryMallShoppingCartItemVOS = new ArrayList<>();
        List<SecondaryMallShoppingCartItem> secondaryMallShoppingCartItems = secondaryMallShoppingCartItemMapper.selectByUserId(secondaryMallUserId, Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER);
        if (!CollectionUtils.isEmpty(secondaryMallShoppingCartItems)) {
            //查询商品信息并做数据转换
            List<Long> secondaryMallGoodsIds = secondaryMallShoppingCartItems.stream().map(SecondaryMallShoppingCartItem::getGoodsId).collect(Collectors.toList());
            List<SecondaryMallGoods> secondaryMallGoods = secondaryMallGoodsMapper.selectByPrimaryKeys(secondaryMallGoodsIds);
            Map<Long, SecondaryMallGoods> secondaryMallGoodsMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(secondaryMallGoods)) {
                secondaryMallGoodsMap = secondaryMallGoods.stream().collect(Collectors.toMap(SecondaryMallGoods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
            }
            for (SecondaryMallShoppingCartItem secondaryMallShoppingCartItem : secondaryMallShoppingCartItems) {
                SecondaryMallShoppingCartItemVO secondaryMallShoppingCartItemVO = new SecondaryMallShoppingCartItemVO();
                BeanUtil.copyProperties(secondaryMallShoppingCartItem, secondaryMallShoppingCartItemVO);
                if (secondaryMallGoodsMap.containsKey(secondaryMallShoppingCartItem.getGoodsId())) {
                    SecondaryMallGoods secondaryMallGoodsTemp = secondaryMallGoodsMap.get(secondaryMallShoppingCartItem.getGoodsId());
                    secondaryMallShoppingCartItemVO.setGoodsCoverImg(secondaryMallGoodsTemp.getGoodsCoverImg());
                    String goodsName = secondaryMallGoodsTemp.getGoodsName();
                    // 字符串过长导致文字超出的问题
                    if (goodsName.length() > 28) {
                        goodsName = goodsName.substring(0, 28) + "...";
                    }
                    secondaryMallShoppingCartItemVO.setGoodsName(goodsName);
                    secondaryMallShoppingCartItemVO.setSellingPrice(secondaryMallGoodsTemp.getSellingPrice());
                    secondaryMallShoppingCartItemVOS.add(secondaryMallShoppingCartItemVO);
                }
            }
        }
        return secondaryMallShoppingCartItemVOS;
    }
}
