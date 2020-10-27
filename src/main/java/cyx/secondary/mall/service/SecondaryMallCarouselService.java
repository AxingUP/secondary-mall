package cyx.secondary.mall.service;

import cyx.secondary.mall.controller.vo.SecondaryMallIndexCarouselVO;
import cyx.secondary.mall.entity.Carousel;
import cyx.secondary.mall.util.PageQueryUtil;
import cyx.secondary.mall.util.PageResult;

import java.util.List;

public interface SecondaryMallCarouselService {
    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getCarouselPage(PageQueryUtil pageUtil);

    String saveCarousel(Carousel carousel);

    String updateCarousel(Carousel carousel);

    Carousel getCarouselById(Integer id);

    Boolean deleteBatch(Integer[] ids);
    /**
     * 返回固定数量的轮播图对象(首页调用)
     *
     * @param number
     * @return
     */
    List<SecondaryMallIndexCarouselVO> getCarouselsForIndex(int number);
}
