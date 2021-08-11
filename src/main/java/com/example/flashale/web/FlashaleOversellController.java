package com.example.flashale.web;

import com.example.flashale.services.FlashaleActivityService;
import com.example.flashale.services.FlashaleOversellService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FlashaleOversellController
{
    @Autowired
    private FlashaleOversellService flashaleOversellService;

    @ResponseBody
//    @RequestMapping("/flashale/{flashaleActivityId}")
    public String flashale(@PathVariable long flashaleActivityId)
    {
        return flashaleOversellService.processFlashale(flashaleActivityId);
    }

    @Autowired
    private FlashaleActivityService flashaleActivityService;

    @ResponseBody
    @RequestMapping("/flashale/{flashaleActivityId}")
    public String flashaleCommodity(@PathVariable long flashaleActivityId)
    {
        boolean stockValidateResult = flashaleActivityService.flashaleStockValidate(flashaleActivityId);
        return stockValidateResult ? "Congratulations snapped up successfully!" : "Product sold out, maybe next time";
    }
}
