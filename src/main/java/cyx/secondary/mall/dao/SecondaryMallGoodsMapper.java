package cyx.secondary.mall.dao;

import cyx.secondary.mall.entity.SecondaryMallGoods;
import cyx.secondary.mall.entity.StockNumDTO;
import cyx.secondary.mall.util.PageQueryUserUtil;
import cyx.secondary.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SecondaryMallGoodsMapper {
    int deleteByPrimaryKey(Long goodsId);

    int insert(SecondaryMallGoods record);

    int insertSelective(SecondaryMallGoods record);

    SecondaryMallGoods selectByPrimaryKey(Long goodsId);

    int updateByPrimaryKeySelective(SecondaryMallGoods record);

    int updateByPrimaryKeyWithBLOBs(SecondaryMallGoods record);

    int updateByPrimaryKey(SecondaryMallGoods record);

    List<SecondaryMallGoods> findSecondaryMallGoodsList(PageQueryUtil pageUtil);

    int getTotalSecondaryMallGoods(PageQueryUtil pageUtil);

    int batchUpdateSellStatus(@Param("orderIds") Long[] orderIds, @Param("sellStatus") int sellStatus);

    List<SecondaryMallGoods> selectByPrimaryKeys(List<Long> goodsIds);

    List<SecondaryMallGoods> findSecondaryMallGoodsListBySearch(PageQueryUtil pageUtil);

    int getTotalSecondaryMallGoodsBySearch(PageQueryUtil pageUtil);

    int updateStockNum(@Param("stockNumDTOS") List<StockNumDTO> stockNumDTOS);

//    用户上传商品列表
    List<SecondaryMallGoods> findSecondaryMallUserSellGoodsList(PageQueryUserUtil pageUserUtil);

//    获得用户上传商品列表总数
    int getTotalSecondaryMallUserSellGoods(PageQueryUserUtil pageUserUtil);
}
