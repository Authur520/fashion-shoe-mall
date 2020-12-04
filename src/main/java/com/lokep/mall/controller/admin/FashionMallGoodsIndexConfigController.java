package com.lokep.mall.controller.admin;

import com.lokep.mall.common.IndexConfigTypeEnum;
import com.lokep.mall.common.ServiceResultEnum;
import com.lokep.mall.entity.FashionMallIndexConfig;
import com.lokep.mall.service.FashionMallGoodsIndexConfigService;
import com.lokep.mall.util.PageQueryUtil;
import com.lokep.mall.util.Result;
import com.lokep.mall.util.ResultGenerator;
import javafx.beans.binding.ObjectExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/admin")
public class FashionMallGoodsIndexConfigController {

    @Autowired
    private FashionMallGoodsIndexConfigService goodsIndexConfigService;

    @GetMapping("/indexConfigs")
    public String indexConfigsPage(HttpServletRequest request, @RequestParam("configType") int configType){
        IndexConfigTypeEnum indexConfigTypeEnum = IndexConfigTypeEnum.getIndexConfigTypeEnumByType(configType);
        if (indexConfigTypeEnum.equals(IndexConfigTypeEnum.DEFAULT)){
            return "首页配置不存在！！！";
        }
        request.setAttribute("path", indexConfigTypeEnum.getName());
        request.setAttribute("configType", configType);
        return "admin/fashion_mall_index_config";
    }
    /**
     * 列表
     */
    @RequestMapping(value = "indexConfigs/list", method = RequestMethod.GET)
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params){
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))){
            return ResultGenerator.genFailResult("参数异常！！！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(goodsIndexConfigService.getConfigsPage(pageUtil));
    }

    /**
     * 新增功能
     * @param indexConfig
     * @return
     */
    @RequestMapping(value = "/indexConfigs/save", method = RequestMethod.POST)
    @ResponseBody
    public Result save(@RequestBody FashionMallIndexConfig indexConfig){
        if (Objects.isNull(indexConfig.getConfigType())
                ||StringUtils.isEmpty(indexConfig.getConfigName())
                ||Objects.isNull(indexConfig.getConfigRank())){
            return ResultGenerator.genFailResult("参数异常！！！");
        }
        String result = goodsIndexConfigService.saveIndexConfig(indexConfig);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 修改
     * @param indexConfig
     * @return
     */
    @RequestMapping(value = "/indexConfigs/update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody FashionMallIndexConfig indexConfig){
        if (Objects.isNull(indexConfig.getConfigType())
                ||Objects.isNull(indexConfig.getConfigId())
                ||StringUtils.isEmpty(indexConfig.getConfigName())
                || Objects.isNull(indexConfig.getConfigRank())){
            return ResultGenerator.genFailResult("参数异常！！！");
        }
        String result = goodsIndexConfigService.updateIndexConfig(indexConfig);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 删除功能
     * @param ids
     * @return
     */
    @RequestMapping(value = "/indexConfigs/delete", method = RequestMethod.POST)
    @ResponseBody
    public Result delete(@RequestBody Long[] ids){
        if (ids.length < 1){
            return ResultGenerator.genFailResult("参数异常！！！");
        }
        if (goodsIndexConfigService.deleteBatch(ids)){
            return ResultGenerator.genSuccessResult();
        }else {
            return ResultGenerator.genFailResult("删除失败！！！");
        }
    }

}
