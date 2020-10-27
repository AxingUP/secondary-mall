package cyx.secondary.mall.controller.mall;

import cyx.secondary.mall.common.Constants;
import cyx.secondary.mall.common.IndexConfigTypeEnum;
import cyx.secondary.mall.controller.vo.SecondaryMallIndexCarouselVO;
import cyx.secondary.mall.controller.vo.SecondaryMallIndexCategoryVO;
import cyx.secondary.mall.controller.vo.SecondaryMallIndexConfigGoodsVO;
import cyx.secondary.mall.service.SecondaryMallCarouselService;
import cyx.secondary.mall.service.SecondaryMallCategoryService;
import cyx.secondary.mall.service.SecondaryMallIndexConfigService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController {

    @Resource
    private SecondaryMallCarouselService secondaryMallCarouselService;

    @Resource
    private SecondaryMallCategoryService secondaryMallCategoryService;

    @Resource
    private SecondaryMallIndexConfigService secondaryMallIndexConfigService;

    @GetMapping({"/index", "/", "/index.html"})
    public String indexPage(HttpServletRequest request) {
        List<SecondaryMallIndexCategoryVO> categories = secondaryMallCategoryService.getCategoriesForIndex();
        List<SecondaryMallIndexCarouselVO> carousels = secondaryMallCarouselService.getCarouselsForIndex(Constants.INDEX_CAROUSEL_NUMBER);
        List<SecondaryMallIndexConfigGoodsVO> hotGoodses = secondaryMallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_HOT.getType(), Constants.INDEX_GOODS_HOT_NUMBER);
        List<SecondaryMallIndexConfigGoodsVO> newGoodses = secondaryMallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_NEW.getType(), Constants.INDEX_GOODS_NEW_NUMBER);
        List<SecondaryMallIndexConfigGoodsVO> recommendGoodses = secondaryMallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_RECOMMOND.getType(), Constants.INDEX_GOODS_RECOMMOND_NUMBER);
        request.setAttribute("categories", categories);//分类数据
        request.setAttribute("carousels", carousels);//轮播图
        request.setAttribute("hotGoodses", hotGoodses);//热销商品
        request.setAttribute("newGoodses", newGoodses);//新品
        request.setAttribute("recommendGoodses", recommendGoodses);//推荐商品
        return "mall/index";
    }
}
