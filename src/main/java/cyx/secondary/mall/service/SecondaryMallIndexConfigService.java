package cyx.secondary.mall.service;

import cyx.secondary.mall.controller.vo.SecondaryMallIndexConfigGoodsVO;
import cyx.secondary.mall.entity.IndexConfig;
import cyx.secondary.mall.util.PageQueryUtil;
import cyx.secondary.mall.util.PageResult;

import java.util.List;

public interface SecondaryMallIndexConfigService {
    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getConfigsPage(PageQueryUtil pageUtil);

    String saveIndexConfig(IndexConfig indexConfig);

    String updateIndexConfig(IndexConfig indexConfig);

    IndexConfig getIndexConfigById(Long id);

    /**
     * 返回固定数量的首页配置商品对象(首页调用)
     *
     * @param number
     * @return
     */
    List<SecondaryMallIndexConfigGoodsVO> getConfigGoodsesForIndex(int configType, int number);

    Boolean deleteBatch(Long[] ids);
}
