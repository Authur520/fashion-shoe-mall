package com.lokep.mall.controller.admin;

import com.lokep.mall.common.ServiceResultEnum;
import com.lokep.mall.entity.FashionMallCarousel;
import com.lokep.mall.service.FashionMallCarouselService;
import com.lokep.mall.util.PageQueryUtil;
import com.lokep.mall.util.Result;
import com.lokep.mall.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/admin")
public class FashionMallCarouselController {

    @Autowired
    private FashionMallCarouselService carouselService;

    @GetMapping("/carousels")
    public String carouselPage(HttpServletRequest request){
        request.setAttribute("path", "newbee_mall_carousel");
        return "admin/fashion_mall_carousel";
    }
    /**
     * 列表
     */
    @RequestMapping(value = "/carousels/list", method = RequestMethod.GET)
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params){
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))){
            return ResultGenerator.genFailResult("参数异常！！！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(carouselService.getCarouselPage(pageUtil));
    }

    /**
     * 添加
     * @param carousel
     * @return
     */
    @RequestMapping(value = "/carousels/save", method = RequestMethod.POST)
    @ResponseBody
    public Result save(@RequestBody FashionMallCarousel carousel){
        if (StringUtils.isEmpty(carousel.getCarouselUrl())|| Objects.isNull(carousel.getCarouselRank())){
            return ResultGenerator.genFailResult("参数异常！！！");
        }
        String result = carouselService.saveCarousel(carousel);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 修改
     * @param carousel
     * @return
     */
    @RequestMapping(value = "/carousels/update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody FashionMallCarousel carousel){
        if (Objects.isNull(carousel.getCarouselId())
                || StringUtils.isEmpty(carousel.getCarouselUrl())
                || Objects.isNull(carousel.getCarouselRank())){
            return ResultGenerator.genFailResult("参数异常！！！");
        }
        String result = carouselService.updateCarousel(carousel);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @RequestMapping(value = "/carousels/delete",method = RequestMethod.POST)
    @ResponseBody
    public Result delete(@RequestBody Integer[] ids){
        if (ids.length < 1){
            ResultGenerator.genFailResult("参数异常！！！");
        }
        if (carouselService.deleteBatch(ids)){
            return ResultGenerator.genSuccessResult();
        }else {
            return ResultGenerator.genFailResult("删除失败");
        }
    }

    /**
     * 详情
     */
    @GetMapping("/carousels/info/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Integer id) {
        FashionMallCarousel carousel = carouselService.getCarouselById(id);
        if (carousel == null) {
            return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        return ResultGenerator.genSuccessResult(carousel);
    }
}
