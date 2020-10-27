package cyx.secondary.mall.controller.mall;


import cyx.secondary.mall.common.Constants;
import cyx.secondary.mall.common.SecondaryMallCategoryLevelEnum;
import cyx.secondary.mall.common.ServiceResultEnum;
import cyx.secondary.mall.controller.vo.SecondaryMallUserVO;
import cyx.secondary.mall.entity.GoodsCategory;
import cyx.secondary.mall.entity.SecondaryMallGoods;
import cyx.secondary.mall.service.SecondaryMallCategoryService;
import cyx.secondary.mall.service.SecondaryMallGoodsService;
import cyx.secondary.mall.util.PageQueryUserUtil;
import cyx.secondary.mall.util.Result;
import cyx.secondary.mall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

//用于用户管理自己发布的商品出售信息
@Controller
@RequestMapping("/mall")
public class UserGoodsController {
    @Resource
    private SecondaryMallGoodsService secondaryMallGoodsService;
    @Resource
    private SecondaryMallCategoryService secondaryMallCategoryService;

    @GetMapping("/goods")
    public String goodsPage(HttpServletRequest request) {
        request.setAttribute("path", "my-sells");
        return "admin/my-sells";
    }

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

    @GetMapping("/goods/edit")
    public String edit(HttpServletRequest request) {
        request.setAttribute("path", "edit");
        //查询所有的一级分类
        List<GoodsCategory> firstLevelCategories = secondaryMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), SecondaryMallCategoryLevelEnum.LEVEL_ONE.getLevel());
        if (!CollectionUtils.isEmpty(firstLevelCategories)) {
            //查询一级分类列表中第一个实体的所有二级分类
            List<GoodsCategory> secondLevelCategories = secondaryMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstLevelCategories.get(0).getCategoryId()), SecondaryMallCategoryLevelEnum.LEVEL_TWO.getLevel());
            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                //查询二级分类列表中第一个实体的所有三级分类
                List<GoodsCategory> thirdLevelCategories = secondaryMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), SecondaryMallCategoryLevelEnum.LEVEL_THREE.getLevel());
                request.setAttribute("firstLevelCategories", firstLevelCategories);
                request.setAttribute("secondLevelCategories", secondLevelCategories);
                request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                request.setAttribute("path", "goods-edit");
                return "admin/my-sells_edit";
            }
        }
        return "error/error_5xx";
    }

    @GetMapping("/goods/edit/{goodsId}")
    public String edit(HttpServletRequest request, @PathVariable("goodsId") Long goodsId) {
        request.setAttribute("path", "edit");
        SecondaryMallGoods secondaryMallGoods = secondaryMallGoodsService.getSecondaryMallGoodsById(goodsId);
        if (secondaryMallGoods == null) {
            return "error/error_400";
        }
        if (secondaryMallGoods.getGoodsCategoryId() > 0) {
            if (secondaryMallGoods.getGoodsCategoryId() != null || secondaryMallGoods.getGoodsCategoryId() > 0) {
                //有分类字段则查询相关分类数据返回给前端以供分类的三级联动显示
                GoodsCategory currentGoodsCategory = secondaryMallCategoryService.getGoodsCategoryById(secondaryMallGoods.getGoodsCategoryId());
                //商品表中存储的分类id字段为三级分类的id，不为三级分类则是错误数据
                if (currentGoodsCategory != null && currentGoodsCategory.getCategoryLevel() == SecondaryMallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
                    //查询所有的一级分类
                    List<GoodsCategory> firstLevelCategories = secondaryMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), SecondaryMallCategoryLevelEnum.LEVEL_ONE.getLevel());
                    //根据parentId查询当前parentId下所有的三级分类
                    List<GoodsCategory> thirdLevelCategories = secondaryMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(currentGoodsCategory.getParentId()), SecondaryMallCategoryLevelEnum.LEVEL_THREE.getLevel());
                    //查询当前三级分类的父级二级分类
                    GoodsCategory secondCategory = secondaryMallCategoryService.getGoodsCategoryById(currentGoodsCategory.getParentId());
                    if (secondCategory != null) {
                        //根据parentId查询当前parentId下所有的二级分类
                        List<GoodsCategory> secondLevelCategories = secondaryMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondCategory.getParentId()), SecondaryMallCategoryLevelEnum.LEVEL_TWO.getLevel());
                        //查询当前二级分类的父级一级分类
                        GoodsCategory firestCategory = secondaryMallCategoryService.getGoodsCategoryById(secondCategory.getParentId());
                        if (firestCategory != null) {
                            //所有分类数据都得到之后放到request对象中供前端读取
                            request.setAttribute("firstLevelCategories", firstLevelCategories);
                            request.setAttribute("secondLevelCategories", secondLevelCategories);
                            request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                            request.setAttribute("firstLevelCategoryId", firestCategory.getCategoryId());
                            request.setAttribute("secondLevelCategoryId", secondCategory.getCategoryId());
                            request.setAttribute("thirdLevelCategoryId", currentGoodsCategory.getCategoryId());
                        }
                    }
                }
            }
        }
        if (secondaryMallGoods.getGoodsCategoryId() == 0) {
            //查询所有的一级分类
            List<GoodsCategory> firstLevelCategories = secondaryMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), SecondaryMallCategoryLevelEnum.LEVEL_ONE.getLevel());
            if (!CollectionUtils.isEmpty(firstLevelCategories)) {
                //查询一级分类列表中第一个实体的所有二级分类
                List<GoodsCategory> secondLevelCategories = secondaryMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstLevelCategories.get(0).getCategoryId()), SecondaryMallCategoryLevelEnum.LEVEL_TWO.getLevel());
                if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                    //查询二级分类列表中第一个实体的所有三级分类
                    List<GoodsCategory> thirdLevelCategories = secondaryMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), SecondaryMallCategoryLevelEnum.LEVEL_THREE.getLevel());
                    request.setAttribute("firstLevelCategories", firstLevelCategories);
                    request.setAttribute("secondLevelCategories", secondLevelCategories);
                    request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                }
            }
        }
        request.setAttribute("goods", secondaryMallGoods);
        request.setAttribute("path", "goods-edit");
        return "admin/my-sells_edit";
    }

    /**
     * 列表
     * test!!!!
     */
    @RequestMapping(value = "/goods/list", method = RequestMethod.GET)
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params,HttpSession httpSession) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
//        SecondaryMallUserVO secondaryMallUserVO = (SecondaryMallUserVO)httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
//        params.put("uid",secondaryMallUserVO.getUserId());
//        PageQueryUserUtil pageUserUtil = new PageQueryUserUtil(params);

        SecondaryMallUserVO secondaryMallUserVO = (SecondaryMallUserVO)httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        params.put("userId",secondaryMallUserVO.getUserId());
        PageQueryUserUtil pageUserUtil = new PageQueryUserUtil(params);


//        pageUserUtil.setUid(secondaryMallUserVO.getUserId());
//        System.out.println( secondaryMallUserVO.getUserId()+"////"+pageUserUtil.getUid());
//        long uu=secondaryMallUserVO.getUserId();
//        System.out.println(uu);
//        System.out.println("---------");
//        pageQueryUserUtil.setUid(uu);
//        long uuu=pageQueryUserUtil.getUid();
//        System.out.println(uuu);
//        System.out.println("controller:"+pageUserUtil.toString());
        return ResultGenerator.genSuccessResult(secondaryMallGoodsService.getSecondaryMallUserSellGoodsPage(pageUserUtil));
    }

    /**
     * 添加
     * test!!!
     */

    @RequestMapping(value = {"/goods/save"}, method = RequestMethod.POST)
    @ResponseBody
    public Result save(@RequestBody SecondaryMallGoods secondaryMallGoods,HttpSession session) {

        if (StringUtils.isEmpty(secondaryMallGoods.getGoodsName())
                || StringUtils.isEmpty(secondaryMallGoods.getGoodsIntro())
                || StringUtils.isEmpty(secondaryMallGoods.getTag())
                || Objects.isNull(secondaryMallGoods.getOriginalPrice())
                || Objects.isNull(secondaryMallGoods.getGoodsCategoryId())
                || Objects.isNull(secondaryMallGoods.getSellingPrice())
                || Objects.isNull(secondaryMallGoods.getStockNum())
                || Objects.isNull(secondaryMallGoods.getGoodsSellStatus())
                || StringUtils.isEmpty(secondaryMallGoods.getGoodsCoverImg())
                || StringUtils.isEmpty(secondaryMallGoods.getGoodsDetailContent())) {
            return ResultGenerator.genFailResult("参数异常！");
        }

        String result = secondaryMallGoodsService.saveSecondaryMallGoods(secondaryMallGoods,session);

        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }


    /**
     * 修改
     * test!!!
     */
    @RequestMapping(value = "/goods/update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody SecondaryMallGoods secondaryMallGoods) {
        if (Objects.isNull(secondaryMallGoods.getGoodsId())
                || StringUtils.isEmpty(secondaryMallGoods.getGoodsName())
                || StringUtils.isEmpty(secondaryMallGoods.getGoodsIntro())
                || StringUtils.isEmpty(secondaryMallGoods.getTag())
                || Objects.isNull(secondaryMallGoods.getOriginalPrice())
                || Objects.isNull(secondaryMallGoods.getSellingPrice())
                || Objects.isNull(secondaryMallGoods.getGoodsCategoryId())
                || Objects.isNull(secondaryMallGoods.getStockNum())
                || Objects.isNull(secondaryMallGoods.getGoodsSellStatus())
                || StringUtils.isEmpty(secondaryMallGoods.getGoodsCoverImg())
                || StringUtils.isEmpty(secondaryMallGoods.getGoodsDetailContent())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = secondaryMallGoodsService.updateSecondaryMallGoods(secondaryMallGoods);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 详情
     */
    @GetMapping("/goods/info/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Long id) {
        SecondaryMallGoods goods = secondaryMallGoodsService.getSecondaryMallGoodsById(id);
        if (goods == null) {
            return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        return ResultGenerator.genSuccessResult(goods);
    }

    /**
     * 批量修改销售状态
     */
    @RequestMapping(value = "/goods/status/{sellStatus}", method = RequestMethod.PUT)
    @ResponseBody
    public Result delete(@RequestBody Long[] ids, @PathVariable("sellStatus") int sellStatus) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (sellStatus != Constants.SELL_STATUS_UP && sellStatus != Constants.SELL_STATUS_DOWN) {
            return ResultGenerator.genFailResult("状态异常！");
        }
        if (secondaryMallGoodsService.batchUpdateSellStatus(ids, sellStatus)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("修改失败");
        }
    }

}
