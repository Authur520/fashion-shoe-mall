package com.lokep.mall.controller.admin;

import com.lokep.mall.common.GoodsCategoryEnum;
import com.lokep.mall.common.ServiceResultEnum;
import com.lokep.mall.entity.FashionMallGoodsCategory;
import com.lokep.mall.service.FashionMallGoodsCategoryService;
import com.lokep.mall.util.PageQueryUtil;
import com.lokep.mall.util.Result;
import com.lokep.mall.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class FashionMallGoodsCategoryController {

    @Autowired
    private FashionMallGoodsCategoryService goodsCategoryService;

    //配置
    @GetMapping("/categories")
    public String categoriesPage(HttpServletRequest request, @RequestParam("categoryLevel") Byte categoryLevel, @RequestParam("parentId") Long parentId, @RequestParam("backParentId") Long backParentId){
        if (categoryLevel == null || categoryLevel < 1 || categoryLevel > 3){
            return "您的页面出错了!!";
        }
        request.setAttribute("path", "newbee_mall_category");
        request.setAttribute("parentId", parentId);
        request.setAttribute("backParentId", backParentId);
        request.setAttribute("categoryLevel", categoryLevel);
        return "admin/fashion_mall_category";
    }

    /**
     * 列表
     * */
    @RequestMapping(value = "/categories/list", method = RequestMethod.GET)
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> param){
        if (StringUtils.isEmpty(param.get("page")) || StringUtils.isEmpty(param.get("limit"))){
            return ResultGenerator.genFailResult("参数异常！！！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(param);
        return ResultGenerator.genSuccessResult(goodsCategoryService.getCategoriesPage(pageUtil));
    }

    /**
     * 列表
     * */
    @RequestMapping(value = "/categories/listForSelect",method = RequestMethod.GET)
    @ResponseBody
    public Result listForSelect(@RequestParam("categoryId") Long categoryId){
        if( categoryId == null || categoryId < 1){
            return ResultGenerator.genFailResult("缺少参数");
        }
        FashionMallGoodsCategory goodsCategoryById = goodsCategoryService.getGoodsCategoryById(categoryId);
        //既不是一级分类又不是二级分类则为不返回数据
        if (goodsCategoryById == null || goodsCategoryById.getCategoryLevel() == GoodsCategoryEnum.LEVEL_THREE.getLevel()){
            return ResultGenerator.genFailResult("参数异常!");
        }
        Map categoryResult = new HashMap(2);
        if (goodsCategoryById.getCategoryLevel() == GoodsCategoryEnum.LEVEL_ONE.getLevel()){
            //如果是一级分类则返回当前一级分类下的所有二级分类，以及二级分类列表中第一条数据下的所有三级分类列表
            //查询一级分类列表中第一个实体的所有二级分类
            List<FashionMallGoodsCategory> secondLevelCategories = goodsCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(categoryId), GoodsCategoryEnum.LEVEL_TWO.getLevel());
            if (!CollectionUtils.isEmpty(secondLevelCategories)){
                //查询二级分类列表中第一个实体的所有三级分类
                List<FashionMallGoodsCategory> thirdLevelCategories = goodsCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), GoodsCategoryEnum.LEVEL_THREE.getLevel());
                categoryResult.put("secondLevelCategories", secondLevelCategories);
                categoryResult.put("thirdLevelCategories", thirdLevelCategories);
            }
        }
        if (goodsCategoryById.getCategoryLevel() == GoodsCategoryEnum.LEVEL_TWO.getLevel()){
            List<FashionMallGoodsCategory> thirdLevelCategories = goodsCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(categoryId), GoodsCategoryEnum.LEVEL_THREE.getLevel());
            categoryResult.put("thirdLevelCategories",thirdLevelCategories);
        }
        return ResultGenerator.genSuccessResult(categoryResult);
    }

    /**
     * 添加
     * @param goodsCategory
     * @return
     */
    @RequestMapping(value = "/categories/save",method = RequestMethod.POST)
    @ResponseBody
    public Result save(@RequestBody FashionMallGoodsCategory goodsCategory){
        if (Objects.isNull(goodsCategory.getCategoryLevel())
                ||StringUtils.isEmpty(goodsCategory.getCategoryName())
                ||Objects.isNull(goodsCategory.getParentId())
                ||Objects.isNull(goodsCategory.getCategoryRank())){
            return ResultGenerator.genFailResult("参数异常！！！");
        }
        String result = goodsCategoryService.saveGoodsCategory(goodsCategory);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 修改
     * @param goodsCategory
     * @return
     */
    @RequestMapping(value = "/categories/update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody FashionMallGoodsCategory goodsCategory){
        if (Objects.isNull(goodsCategory.getCategoryId())
                ||Objects.isNull(goodsCategory.getCategoryLevel())
                ||StringUtils.isEmpty(goodsCategory.getCategoryName())
                ||Objects.isNull(goodsCategory.getParentId())
                ||Objects.isNull(goodsCategory.getCategoryRank())){
            return ResultGenerator.genFailResult("参数异常！！！");
        }
        String result = goodsCategoryService.updateGoodsCategory(goodsCategory);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 分类删除
     * @param ids
     * @return
     */
    @RequestMapping(value = "/categories/delete", method = RequestMethod.POST)
    @ResponseBody
    public Result delete(@RequestBody Integer[] ids){
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！！！");
        }
        if (goodsCategoryService.deleteBatch(ids)){
            return ResultGenerator.genSuccessResult();
        }else {
            return ResultGenerator.genFailResult("删除失败");
        }
    }
}
