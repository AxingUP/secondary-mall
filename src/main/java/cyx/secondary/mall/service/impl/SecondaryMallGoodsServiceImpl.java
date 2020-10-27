package cyx.secondary.mall.service.impl;

import cyx.secondary.mall.common.Constants;
import cyx.secondary.mall.common.ServiceResultEnum;
import cyx.secondary.mall.controller.vo.AdminUserVO;
import cyx.secondary.mall.controller.vo.SecondaryMallSearchGoodsVO;
import cyx.secondary.mall.controller.vo.SecondaryMallUserVO;
import cyx.secondary.mall.dao.SecondaryMallGoodsMapper;
import cyx.secondary.mall.entity.SecondaryMallGoods;
import cyx.secondary.mall.service.SecondaryMallGoodsService;
import cyx.secondary.mall.util.BeanUtil;
import cyx.secondary.mall.util.PageQueryUserUtil;
import cyx.secondary.mall.util.PageQueryUtil;
import cyx.secondary.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

@Service
public class SecondaryMallGoodsServiceImpl implements SecondaryMallGoodsService {
    @Autowired
    private SecondaryMallGoodsMapper goodsMapper;

    @Override
    public PageResult getSecondaryMallGoodsPage(PageQueryUtil pageUtil) {
        List<SecondaryMallGoods> goodsList = goodsMapper.findSecondaryMallGoodsList(pageUtil);
        int total = goodsMapper.getTotalSecondaryMallGoods(pageUtil);
        PageResult pageResult = new PageResult(goodsList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

//    test 显示用户上传的二手商品
    @Override
    public PageResult getSecondaryMallUserSellGoodsPage(PageQueryUserUtil pageUserUtil) {
//        System.out.println("service:"+pageUserUtil.toString());
        List<SecondaryMallGoods> goodsList = goodsMapper.findSecondaryMallUserSellGoodsList(pageUserUtil);
        int total = goodsMapper.getTotalSecondaryMallUserSellGoods(pageUserUtil);
        PageResult pageResult = new PageResult(goodsList, total, pageUserUtil.getLimit(),pageUserUtil.getPage());
        return pageResult;
    }

//    保存商品
    @Override
    public String saveSecondaryMallGoods(SecondaryMallGoods goods, HttpSession httpSession) {
        Enumeration<String> attrs = httpSession.getAttributeNames();
            // 遍历attrs中的
        while(attrs.hasMoreElements()){
            // 获取session键值
            String name = attrs.nextElement().toString();
            // 判定是用户还是管理员
            if (name.equals("adminUser"))
            {
                AdminUserVO adminUserVO = (AdminUserVO)httpSession.getAttribute(Constants.ADMIN_USER_SESSION_KEY);
//                System.out.println(adminUserVO.toString());
                Long userId = adminUserVO.getAdminUserId();
//                //设置添加时间
                goods.setCreateTime(new Date());
                goods.setCreateUser(Integer.valueOf(String.valueOf(userId)));}
            else if (name == "secondaryMallUser"){
                SecondaryMallUserVO secondaryMallUserVO = (SecondaryMallUserVO)httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
                System.out.println("*******************");
                System.out.println(secondaryMallUserVO.toString());
                Long userId = secondaryMallUserVO.getUserId();
//                System.out.println("@@@@@");
//                System.out.println(userId);
                goods.setCreateUser(Integer.valueOf(String.valueOf(userId)));
//                System.out.println(goods.getCreateUser());
//                //设置添加时间
                goods.setCreateTime(new Date());
            }
        }

        if (goodsMapper.insertSelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateSecondaryMallGoods(SecondaryMallGoods goods) {
        SecondaryMallGoods temp = goodsMapper.selectByPrimaryKey(goods.getGoodsId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        goods.setUpdateTime(new Date());
        if (goodsMapper.updateByPrimaryKeySelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public SecondaryMallGoods getSecondaryMallGoodsById(Long id) {
        return  goodsMapper.selectByPrimaryKey(id);
    }

    @Override
    public Boolean batchUpdateSellStatus(Long[] ids, int sellStatus) {
        return goodsMapper.batchUpdateSellStatus(ids, sellStatus) > 0;
    }

    @Override
    public PageResult searchSecondaryMallGoods(PageQueryUtil pageUtil) {
        List<SecondaryMallGoods> goodsList = goodsMapper.findSecondaryMallGoodsListBySearch(pageUtil);
        int total = goodsMapper.getTotalSecondaryMallGoodsBySearch(pageUtil);
        List<SecondaryMallSearchGoodsVO> secondaryMallSearchGoodsVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsList)) {
            secondaryMallSearchGoodsVOS = BeanUtil.copyList(goodsList, SecondaryMallSearchGoodsVO.class);
            for (SecondaryMallSearchGoodsVO secondaryMallSearchGoodsVO : secondaryMallSearchGoodsVOS) {
                String goodsName = secondaryMallSearchGoodsVO.getGoodsName();
                String goodsIntro = secondaryMallSearchGoodsVO.getGoodsIntro();
                // 字符串过长导致文字超出的问题
                if (goodsName.length() > 28) {
                    goodsName = goodsName.substring(0, 28) + "...";
                    secondaryMallSearchGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 30) {
                    goodsIntro = goodsIntro.substring(0, 30) + "...";
                    secondaryMallSearchGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        PageResult pageResult = new PageResult(secondaryMallSearchGoodsVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }
}
