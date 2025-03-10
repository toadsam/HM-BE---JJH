package com.example.HM;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {

    @RequestMapping("/list")
    @ResponseBody
    public String list()
    {
        return "게시물목록";
    }


}
