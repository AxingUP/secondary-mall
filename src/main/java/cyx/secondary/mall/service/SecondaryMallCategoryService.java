package cyx.secondary.mall.service;

import cyx.secondary.mall.controller.vo.SearchPageCategoryVO;
import cyx.secondary.mall.controller.vo.SecondaryMallIndexCategoryVO;
import cyx.secondary.mall.entity.GoodsCategory;
import cyx.secondary.mall.util.PageQueryUtil;
import cyx.secondary.mall.util.PageResult;

import java.util.List;

public interface SecondaryMallCategoryService {
    /**
     * 后台分页
     *
     * @param pageQueryUtil
     * @return
     */
    PageResult getCategorisPage(PageQueryUtil pageQueryUtil);

    String saveCategory(GoodsCategory goodsCategory);

    String updateGoodsCategory(GoodsCategory goodsCategory);

    GoodsCategory getGoodsCategoryById(Long id);

    Boolean deleteBatch(Integer[] ids);

    /**
     * 根据parentId和level获取分类列表
     *
     * @param parentIds
     * @param categoryLevel
     * @return
     */
    List<GoodsCategory> selectByLevelAndParentIdsAndNumber(List<Long> parentIds, int categoryLevel);

    /**
     * 返回分类数据(首页调用)
     *
     * @return
     */
    List<SecondaryMallIndexCategoryVO> getCategoriesForIndex();

    /**
     * 返回分类数据(搜索页调用)
     *
     * @param categoryId
     * @return
     */
    SearchPageCategoryVO getCategoriesForSearch(Long categoryId);
}
