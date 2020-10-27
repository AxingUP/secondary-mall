package cyx.secondary.mall.controller.admin;

import cyx.secondary.mall.common.SecondaryMallCategoryLevelEnum;
import cyx.secondary.mall.common.ServiceResultEnum;
import cyx.secondary.mall.entity.GoodsCategory;
import cyx.secondary.mall.service.SecondaryMallCategoryService;
import cyx.secondary.mall.util.PageQueryUtil;
import cyx.secondary.mall.util.Result;
import cyx.secondary.mall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class SecondaryMallGoodsCategoryController {

    @Resource
    private SecondaryMallCategoryService secondaryMallCategoryService;

    @GetMapping("/categories")
    public String categoriesPage(HttpServletRequest request, @RequestParam("categoryLevel") Byte categoryLevel, @RequestParam("parentId") Long parentId, @RequestParam("backParentId") Long backParentId) {
        if (categoryLevel == null || categoryLevel < 1 || categoryLevel > 3) {
            return "error/error_5xx";
        }
        request.setAttribute("path", "secondary_mall_category");
        request.setAttribute("parentId", parentId);
        request.setAttribute("backParentId", backParentId);
        request.setAttribute("categoryLevel", categoryLevel);
        return "admin/secondary_mall_category";
    }

//    @GetMapping("/coupling-test")
//    public String couplingTest(HttpServletRequest request) {
//        request.setAttribute("path", "coupling-test");
//        //查询所有的一级分类
//        List<GoodsCategory> firstLevelCategories = secondaryMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), SecondaryMallCategoryLevelEnum.LEVEL_ONE.getLevel());
//        if (!CollectionUtils.isEmpty(firstLevelCategories)) {
//            //查询一级分类列表中第一个实体的所有二级分类
//            List<GoodsCategory> secondLevelCategories = secondaryMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstLevelCategories.get(0).getCategoryId()), SecondaryMallCategoryLevelEnum.LEVEL_TWO.getLevel());
//            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
//                //查询二级分类列表中第一个实体的所有三级分类
//                List<GoodsCategory> thirdLevelCategories = secondaryMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), SecondaryMallCategoryLevelEnum.LEVEL_THREE.getLevel());
//                request.setAttribute("firstLevelCategories", firstLevelCategories);
//                request.setAttribute("secondLevelCategories", secondLevelCategories);
//                request.setAttribute("thirdLevelCategories", thirdLevelCategories);
//                return "admin/coupling-test";
//            }
//        }
//        return "error/error_5xx";
//    }

    /**
     * 列表
     */
    @RequestMapping(value = "/categories/listForSelect", method = RequestMethod.GET)
    @ResponseBody
    public Result listForSelect(@RequestParam("categoryId") Long categoryId) {
        if (categoryId == null || categoryId < 1) {
            return ResultGenerator.genFailResult("缺少参数！");
        }
        GoodsCategory category = secondaryMallCategoryService.getGoodsCategoryById(categoryId);
        //既不是一级分类也不是二级分类则为不返回数据
        if (category == null || category.getCategoryLevel() == SecondaryMallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        Map categoryResult = new HashMap(2);
        if (category.getCategoryLevel() == SecondaryMallCategoryLevelEnum.LEVEL_ONE.getLevel()) {
            //如果是一级分类则返回当前一级分类下的所有二级分类，以及二级分类列表中第一条数据下的所有三级分类列表
            //查询一级分类列表中第一个实体的所有二级分类
            List<GoodsCategory> secondLevelCategories = secondaryMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(categoryId), SecondaryMallCategoryLevelEnum.LEVEL_TWO.getLevel());
            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                //查询二级分类列表中第一个实体的所有三级分类
                List<GoodsCategory> thirdLevelCategories = secondaryMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), SecondaryMallCategoryLevelEnum.LEVEL_THREE.getLevel());
                categoryResult.put("secondLevelCategories", secondLevelCategories);
                categoryResult.put("thirdLevelCategories", thirdLevelCategories);
            }
        }
        if (category.getCategoryLevel() == SecondaryMallCategoryLevelEnum.LEVEL_TWO.getLevel()) {
            //如果是二级分类则返回当前分类下的所有三级分类列表
            List<GoodsCategory> thirdLevelCategories = secondaryMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(categoryId), SecondaryMallCategoryLevelEnum.LEVEL_THREE.getLevel());
            categoryResult.put("thirdLevelCategories", thirdLevelCategories);
        }
        return ResultGenerator.genSuccessResult(categoryResult);
    }

    /**
     * 列表
     */
    @RequestMapping(value = "/categories/list", method = RequestMethod.GET)
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit")) || StringUtils.isEmpty(params.get("categoryLevel")) || StringUtils.isEmpty(params.get("parentId"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(secondaryMallCategoryService.getCategorisPage(pageUtil));
    }

    /**
     * 添加
     */
    @RequestMapping(value = "/categories/save", method = RequestMethod.POST)
    @ResponseBody
    public Result save(@RequestBody GoodsCategory goodsCategory) {
        if (Objects.isNull(goodsCategory.getCategoryLevel())
                || StringUtils.isEmpty(goodsCategory.getCategoryName())
                || Objects.isNull(goodsCategory.getParentId())
                || Objects.isNull(goodsCategory.getCategoryRank())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = secondaryMallCategoryService.saveCategory(goodsCategory);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }


    /**
     * 修改
     */
    @RequestMapping(value = "/categories/update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody GoodsCategory goodsCategory) {
        if (Objects.isNull(goodsCategory.getCategoryId())
                || Objects.isNull(goodsCategory.getCategoryLevel())
                || StringUtils.isEmpty(goodsCategory.getCategoryName())
                || Objects.isNull(goodsCategory.getParentId())
                || Objects.isNull(goodsCategory.getCategoryRank())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = secondaryMallCategoryService.updateGoodsCategory(goodsCategory);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 详情
     */
    @GetMapping("/categories/info/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Long id) {
        GoodsCategory goodsCategory = secondaryMallCategoryService.getGoodsCategoryById(id);
        if (goodsCategory == null) {
            return ResultGenerator.genFailResult("未查询到数据");
        }
        return ResultGenerator.genSuccessResult(goodsCategory);
    }

    /**
     * 分类删除
     */
    @RequestMapping(value = "/categories/delete", method = RequestMethod.POST)
    @ResponseBody
    public Result delete(@RequestBody Integer[] ids) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (secondaryMallCategoryService.deleteBatch(ids)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("删除失败");
        }
    }


}