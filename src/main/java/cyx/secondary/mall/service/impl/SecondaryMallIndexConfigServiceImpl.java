package cyx.secondary.mall.service.impl;

import cyx.secondary.mall.common.ServiceResultEnum;
import cyx.secondary.mall.controller.vo.SecondaryMallIndexConfigGoodsVO;
import cyx.secondary.mall.dao.IndexConfigMapper;
import cyx.secondary.mall.dao.SecondaryMallGoodsMapper;
import cyx.secondary.mall.entity.IndexConfig;
import cyx.secondary.mall.entity.SecondaryMallGoods;
import cyx.secondary.mall.service.SecondaryMallIndexConfigService;
import cyx.secondary.mall.util.BeanUtil;
import cyx.secondary.mall.util.PageQueryUtil;
import cyx.secondary.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SecondaryMallIndexConfigServiceImpl implements SecondaryMallIndexConfigService {


    @Autowired
    private IndexConfigMapper indexConfigMapper;

    @Autowired
    private SecondaryMallGoodsMapper goodsMapper;

    @Override
    public PageResult getConfigsPage(PageQueryUtil pageUtil) {
        List<IndexConfig> indexConfigs = indexConfigMapper.findIndexConfigList(pageUtil);
        int total = indexConfigMapper.getTotalIndexConfigs(pageUtil);
        PageResult pageResult = new PageResult(indexConfigs, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveIndexConfig(IndexConfig indexConfig) {
        //todo 判断是否存在该商品
        if (indexConfigMapper.insertSelective(indexConfig) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateIndexConfig(IndexConfig indexConfig) {
        //todo 判断是否存在该商品
        IndexConfig temp = indexConfigMapper.selectByPrimaryKey(indexConfig.getConfigId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        if (indexConfigMapper.updateByPrimaryKeySelective(indexConfig) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public IndexConfig getIndexConfigById(Long id) {
        return null;
    }

    @Override
    public List<SecondaryMallIndexConfigGoodsVO> getConfigGoodsesForIndex(int configType, int number) {
        List<SecondaryMallIndexConfigGoodsVO> secondaryMallIndexConfigGoodsVOS = new ArrayList<>(number);
        List<IndexConfig> indexConfigs = indexConfigMapper.findIndexConfigsByTypeAndNum(configType, number);
        if (!CollectionUtils.isEmpty(indexConfigs)) {
            //取出所有的goodsId
            List<Long> goodsIds = indexConfigs.stream().map(IndexConfig::getGoodsId).collect(Collectors.toList());
            List<SecondaryMallGoods> secondaryMallGoods = goodsMapper.selectByPrimaryKeys(goodsIds);
            secondaryMallIndexConfigGoodsVOS = BeanUtil.copyList(secondaryMallGoods, SecondaryMallIndexConfigGoodsVO.class);
            for (SecondaryMallIndexConfigGoodsVO secondaryMallIndexConfigGoodsVO : secondaryMallIndexConfigGoodsVOS) {
                String goodsName = secondaryMallIndexConfigGoodsVO.getGoodsName();
                String goodsIntro = secondaryMallIndexConfigGoodsVO.getGoodsIntro();
                // 字符串过长导致文字超出的问题
                if (goodsName.length() > 30) {
                    goodsName = goodsName.substring(0, 30) + "...";
                    secondaryMallIndexConfigGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 22) {
                    goodsIntro = goodsIntro.substring(0, 22) + "...";
                    secondaryMallIndexConfigGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        return secondaryMallIndexConfigGoodsVOS;
    }

    @Override
    public Boolean deleteBatch(Long[] ids) {
        if (ids.length < 1) {
            return false;
        }
        //删除数据
        return indexConfigMapper.deleteBatch(ids) > 0;
    }
}
