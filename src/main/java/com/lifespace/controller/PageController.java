package com.lifespace.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/lifespace")
public class PageController {

    //前台
    @GetMapping("/orders")
    public String toOrders(){
        return "front-end/orders";
    }

    @GetMapping("/homepage")
    public String toHomePage(){
        return "front-end/homepage";
    }

    @GetMapping("/login")
    public String toAdd_branch(){
        return "/front-end/login";
    }
//
//    @GetMapping("/add_remtal")
//    public String toHomePage(){
//        return "/front-end/homepage";
//    }
//
//    @GetMapping("/")
//    public String toHomePage(){
//        return "/front-end/homepage";
//    }
//
//    @GetMapping("/")
//    public String toHomePage(){
//        return "/front-end/homepage";
//    }
//
//    @GetMapping("/")
//    public String toHomePage(){
//        return "/front-end/homepage";
//    }
//
//    @GetMapping("/")
//    public String toHomePage(){
//        return "/front-end/homepage";
//    }
//
//    @GetMapping("/")
//    public String toHomePage(){
//        return "/front-end/homepage";
//    }
//
//    @GetMapping("/")
//    public String toHomePage(){
//        return "/front-end/homepage";
//    }
//
//    @GetMapping("/")
//    public String toHomePage(){
//        return "/front-end/homepage";
//    }
//
//    @GetMapping("/")
//    public String toHomePage(){
//        return "/front-end/homepage";
//    }
//
//    @GetMapping("/")
//    public String toHomePage(){
//        return "/front-end/homepage";
//    }
//
//
//    //後台
//    @GetMapping("/")
//    public String toHomePage(){
//        return "/front-end/homepage";
//    }
//
//    @GetMapping("/")
//    public String toHomePage(){
//        return "/front-end/homepage";
//    }
//
//    @GetMapping("/")
//    public String toHomePage(){
//        return "/front-end/homepage";
//    }
//
//    @GetMapping("/")
//    public String toHomePage(){
//        return "/front-end/homepage";
//    }
//
//    @GetMapping("/")
//    public String toHomePage(){
//        return "/front-end/homepage";
//    }
//
//    @GetMapping("/")
//    public String toHomePage(){
//        return "/front-end/homepage";
//    }
//
//    @GetMapping("/")
//    public String toHomePage(){
//        return "/front-end/homepage";
//    }
//
//    @GetMapping("/")
//    public String toHomePage(){
//        return "/front-end/homepage";
//    }
//
}

