package cyx.secondary.mall.dao;

import cyx.secondary.mall.entity.SecondaryMallShoppingCartItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SecondaryMallShoppingCartItemMapper {
    int deleteByPrimaryKey(Long cartItemId);

    int insert(SecondaryMallShoppingCartItem record);

    int insertSelective(SecondaryMallShoppingCartItem record);

    SecondaryMallShoppingCartItem selectByPrimaryKey(Long cartItemId);

    SecondaryMallShoppingCartItem selectByUserIdAndGoodsId(@Param("secondaryMallUserId") Long secondaryMallUserId, @Param("goodsId") Long goodsId);

    List<SecondaryMallShoppingCartItem> selectByUserId(@Param("secondaryMallUserId") Long secondaryMallUserId, @Param("number") int number);

    int selectCountByUserId(Long newBeeMallUserId);

    int updateByPrimaryKeySelective(SecondaryMallShoppingCartItem record);

    int updateByPrimaryKey(SecondaryMallShoppingCartItem record);

    int deleteBatch(List<Long> ids);
}
