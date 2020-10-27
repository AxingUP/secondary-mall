package cyx.secondary.mall.service;

import cyx.secondary.mall.entity.SecondaryMallGoods;
import cyx.secondary.mall.util.PageQueryUserUtil;
import cyx.secondary.mall.util.PageQueryUtil;
import cyx.secondary.mall.util.PageResult;

import javax.servlet.http.HttpSession;

public interface SecondaryMallGoodsService {
    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getSecondaryMallGoodsPage(PageQueryUtil pageUtil);

    /**
     * test 用户上传的二手商品
     *
     * @param pageUtil
     * @return
     */
    PageResult getSecondaryMallUserSellGoodsPage(PageQueryUserUtil pageUserUtil);

    /**
     * 添加商品
     *
     * @param goods
     * @return
     */
    String saveSecondaryMallGoods(SecondaryMallGoods goods, HttpSession httpSession);

    /**
     * 修改商品信息
     *
     * @param goods
     * @return
     */
    String updateSecondaryMallGoods(SecondaryMallGoods goods);

    /**
     * 获取商品详情
     *
     * @param id
     * @return
     */
    SecondaryMallGoods getSecondaryMallGoodsById(Long id);

    /**
     * 批量修改销售状态(上架下架)
     *
     * @param ids
     * @return
     */
    Boolean batchUpdateSellStatus(Long[] ids, int sellStatus);


    /**
     * 商品搜索
     *
     * @param pageUtil
     * @return
     */
    PageResult searchSecondaryMallGoods(PageQueryUtil pageUtil);
}
