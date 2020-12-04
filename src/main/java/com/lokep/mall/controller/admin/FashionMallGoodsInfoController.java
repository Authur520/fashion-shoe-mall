package com.lokep.mall.controller.admin;

import com.lokep.mall.common.Constants;
import com.lokep.mall.common.GoodsCategoryEnum;
import com.lokep.mall.common.ServiceResultEnum;
import com.lokep.mall.entity.FashionMallGoodsCategory;
import com.lokep.mall.entity.FashionMallGoodsInfo;
import com.lokep.mall.service.FashionMallGoodsCategoryService;
import com.lokep.mall.service.FashionMallGoodsInfoService;
import com.lokep.mall.util.PageQueryUtil;
import com.lokep.mall.util.Result;
import com.lokep.mall.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/admin")
public class FashionMallGoodsInfoController {

    @Autowired
    private FashionMallGoodsInfoService goodsInfoService;

    @Autowired
    private FashionMallGoodsCategoryService goodsCategoryService;

    @GetMapping("/goods")
    public String goodsPage(HttpServletRequest request){
        request.setAttribute("path","newbee_mall_goods");
        return "admin/fashion_mall_goods";
    }

    @GetMapping("/goods/edit")
    public String edit(HttpServletRequest request) {
        request.setAttribute("path", "edit");
        //查询所有的一级分类
        List<FashionMallGoodsCategory> firstLevelCategories = goodsCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), GoodsCategoryEnum.LEVEL_ONE.getLevel());
        if (!CollectionUtils.isEmpty(firstLevelCategories)) {
            //查询一级分类列表中第一个实体的所有二级分类
            List<FashionMallGoodsCategory> secondLevelCategories = goodsCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstLevelCategories.get(0).getCategoryId()), GoodsCategoryEnum.LEVEL_TWO.getLevel());
            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                //查询二级分类列表中第一个实体的所有三级分类
                List<FashionMallGoodsCategory> thirdLevelCategories = goodsCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), GoodsCategoryEnum.LEVEL_THREE.getLevel());
                request.setAttribute("firstLevelCategories", firstLevelCategories);
                request.setAttribute("secondLevelCategories", secondLevelCategories);
                request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                request.setAttribute("path", "goods-edit");
                return "admin/fashion_mall_goods_edit";
            }
        }
        return "您的页面出错了...";
    }

    // @GetMapping("/goods/edit")
    // public String edit(HttpServletRequest request){
    //     request.setAttribute("path","edit");
    //     //查询所有的一级分类
    //     List<FashionMallGoodsCategory> firstLevelCategories = goodsCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), GoodsCategoryEnum.LEVEL_ONE.getLevel());
    //     if (!CollectionUtils.isEmpty(firstLevelCategories)){
    //         //查询一级分类列表中第一个实体的所有二级分类
    //         List<FashionMallGoodsCategory> secondLevelCategories = goodsCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstLevelCategories.get(0).getCategoryId()), GoodsCategoryEnum.LEVEL_TWO.getLevel());
    //         if (!CollectionUtils.isEmpty(secondLevelCategories)){
    //             //查询二级分类列表中第一个实体的所有三级分类
    //             List<FashionMallGoodsCategory> thirdLevelCategories = goodsCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), GoodsCategoryEnum.LEVEL_THREE.getLevel());
    //             request.setAttribute("firstLevelCategories",firstLevelCategories);
    //             request.setAttribute("secondLevelCategories", secondLevelCategories);
    //             request.setAttribute("thirdLevelCategories", thirdLevelCategories);
    //             request.setAttribute("path", "goods-edit");
    //             return "admin/fashion_mall_goods_edit";
    //         }
    //     }
    //     return "您的页面出错了！！！";
    // }

    @GetMapping("/goods/edit/{goodsId}")
    public String edit(HttpServletRequest request, @PathVariable("goodsId") Long goodsId){
        request.setAttribute("path", "edit");
        FashionMallGoodsInfo goodsInfo = goodsInfoService.getFashionMallGoodsById(goodsId);
        if (goodsInfo == null){
            return "您的页面出错了！！！";
        }
        if (goodsInfo.getGoodsCategoryId() > 0){
            if (goodsInfo.getGoodsCategoryId() != null || goodsInfo.getGoodsCategoryId() > 0 ){
                //有分类字段则查询相关分类数据返回给前端以供分类的三级联动显示
                FashionMallGoodsCategory goodsCategory = goodsCategoryService.getGoodsCategoryById(goodsInfo.getGoodsCategoryId());
                //商品表中存储的分类id字段为三级分类的id，不为三级分类则是错误数据
                if (goodsCategory != null || goodsCategory.getCategoryLevel() == GoodsCategoryEnum.LEVEL_THREE.getLevel()){
                    //查询所有的一级分类
                    List<FashionMallGoodsCategory> firstLevelCategories = goodsCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), GoodsCategoryEnum.LEVEL_ONE.getLevel());
                    //根据parentId查询当前parentId下所有的三级分类
                    List<FashionMallGoodsCategory> thirdLevelCategories = goodsCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(goodsCategory.getParentId()), GoodsCategoryEnum.LEVEL_THREE.getLevel());
                    //查询当前三级分类的父级二级分类
                    FashionMallGoodsCategory secondCategory = goodsCategoryService.getGoodsCategoryById(goodsCategory.getParentId());
                    if (secondCategory != null){
                        //根据parentId查询当前parentId下所有的二级分类
                        List<FashionMallGoodsCategory> secondLevelCategories = goodsCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondCategory.getParentId()), GoodsCategoryEnum.LEVEL_TWO.getLevel());
                        //查询当前二级分类的父级一级分类
                        FashionMallGoodsCategory firstCategory = goodsCategoryService.getGoodsCategoryById(secondCategory.getParentId());
                        if (firstCategory != null){
                            //所有分类数据都得到之后放到request对象中供前端读取
                            request.setAttribute("firstLevelCategories",firstLevelCategories);
                            request.setAttribute("secondLevelCategories",secondLevelCategories);
                            request.setAttribute("thirdLevelCategories",thirdLevelCategories);
                            request.setAttribute("firstLevelCategoryId",firstCategory.getCategoryId());
                            request.setAttribute("secondLevelCategoryId",secondCategory.getCategoryId());
                            request.setAttribute("thirdLevelCategoryId",goodsCategory.getCategoryId());
                        }
                    }
                }
            }
        }
        if (goodsInfo.getGoodsCategoryId() == 0){
            //查询所有的一级分类
            List<FashionMallGoodsCategory> firstLevelCategories = goodsCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), GoodsCategoryEnum.LEVEL_ONE.getLevel());
            if (!CollectionUtils.isEmpty(firstLevelCategories)){
                //查询一级分类列表中第一个实体的所有二级分类
                List<FashionMallGoodsCategory> secondLevelCategories = goodsCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstLevelCategories.get(0).getCategoryId()), GoodsCategoryEnum.LEVEL_TWO.getLevel());
                if (!CollectionUtils.isEmpty(secondLevelCategories)){
                    //查询二级分类列表中第一个实体的所以二级分类
                    List<FashionMallGoodsCategory> thirdLevelCategories = goodsCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), GoodsCategoryEnum.LEVEL_THREE.getLevel());
                    request.setAttribute("firstLevelCategories", firstLevelCategories);
                    request.setAttribute("secondLevelCategories", secondLevelCategories);
                    request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                }
            }
        }
        request.setAttribute("goods", goodsInfo);
        request.setAttribute("path", "goods-edit");
        return "admin/fashion_mall_goods_edit";
    }
    /**
     * 列表
     * */
    @RequestMapping(value = "/goods/list", method = RequestMethod.GET)
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params){
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))){
            return ResultGenerator.genFailResult("参数异常！！！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(goodsInfoService.getFashionMallGoodsPage(pageUtil));
    }

    /**
     * 添加商品
     * @param goodsInfo
     * @return
     */
    @RequestMapping(value = "/goods/save", method = RequestMethod.POST)
    @ResponseBody
    public Result save(@RequestBody FashionMallGoodsInfo goodsInfo) {
        if (StringUtils.isEmpty(goodsInfo.getGoodsName())
                || StringUtils.isEmpty(goodsInfo.getGoodsIntro())
                || StringUtils.isEmpty(goodsInfo.getTag())
                || Objects.isNull(goodsInfo.getOriginalPrice())
                || Objects.isNull(goodsInfo.getGoodsCategoryId())
                || Objects.isNull(goodsInfo.getSellingPrice())
                || Objects.isNull(goodsInfo.getStockNum())
                || Objects.isNull(goodsInfo.getGoodsSellStatus())
                || StringUtils.isEmpty(goodsInfo.getGoodsCoverImg())
                || StringUtils.isEmpty(goodsInfo.getGoodsDetailContent())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = goodsInfoService.saveGoodsInfo(goodsInfo);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 修改
     * @param goodsInfo
     * @return
     */
    @RequestMapping(value = "/goods/update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody FashionMallGoodsInfo goodsInfo) {
        if (Objects.isNull(goodsInfo.getGoodsId())
                || StringUtils.isEmpty(goodsInfo.getGoodsName())
                || StringUtils.isEmpty(goodsInfo.getGoodsIntro())
                || StringUtils.isEmpty(goodsInfo.getTag())
                || Objects.isNull(goodsInfo.getOriginalPrice())
                || Objects.isNull(goodsInfo.getSellingPrice())
                || Objects.isNull(goodsInfo.getGoodsCategoryId())
                || Objects.isNull(goodsInfo.getStockNum())
                || Objects.isNull(goodsInfo.getGoodsSellStatus())
                || StringUtils.isEmpty(goodsInfo.getGoodsCoverImg())
                || StringUtils.isEmpty(goodsInfo.getGoodsDetailContent())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = goodsInfoService.updateGoodsInfo(goodsInfo);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 批量修改销售状态
     */
    @RequestMapping(value = "/goods/status/{sellStatus}",method = RequestMethod.PUT)
    @ResponseBody
    public Result updateStatus(@RequestBody Long[] ids, @PathVariable("sellStatus") int sellStatus){
        if (ids.length < 1){
            return ResultGenerator.genFailResult("参数异常！！！");
        }
        if (sellStatus != Constants.SELL_STATUS_UP && sellStatus != Constants.SELL_STATUS_DOWN){
            ResultGenerator.genFailResult("状态异常！！！");
        }if (goodsInfoService.batchUpdateSellStatus(ids, sellStatus)){
            return ResultGenerator.genSuccessResult();
        }else {
            return ResultGenerator.genFailResult("修改失败");
        }
    }

}
